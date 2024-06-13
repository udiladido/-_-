import { API_BASE_URL } from "../app-config";
const ACCESS_TOKEN = "ACCESS-TOKEN";

export function call(api, method, request) {
    let headers = new Headers({
        "Content-Type": "application/json",
    });

    const accessToken = localStorage.getItem(ACCESS_TOKEN);
    if (accessToken) {
        headers.append("Authorization", "Bearer " + accessToken);
    }

    let options = {
        headers: headers,
        url: API_BASE_URL + api,
        method: method,
    };

    if (request) {
        options.body = JSON.stringify(request);
    }

    return fetch(options.url, options).then((response) =>
        response.json().then((json) => {
            if (!response.ok) {
                return Promise.reject(json);
            }
            return json;
        })
    ).catch((error) => {
        console.log(error.status);
        if (error.status === 403) {
            window.location.href = "/login";
        }
        return Promise.reject(error);
    });
}

export function signin(userDTO) {
    return call("/auth/signin", "POST", userDTO).then((response) => {
        if (response.token) {
            localStorage.setItem(ACCESS_TOKEN, response.token);
            window.location.href = "/";
        } else {
            throw new Error("로그인 실패: 토큰을 받지 못했습니다.");
        }
    }).catch((error) => {
        console.error("로그인 실패", error);
    });
}

export function signup(userDTO) {
    return call("/auth/signup", "POST", userDTO).then((response) => {
        if (response.id) {
            window.location.href = "/";
        }
    }).catch((error) => {
        console.log("Oops!");
        console.log(error.status);
        if (error.status === 403) {
            window.location.href = "/auth/signup";
        }
        return Promise.reject(error);
    });
}

export function signout() {
    localStorage.removeItem(ACCESS_TOKEN);
    window.location.href = "/login";
}


export function isAuthenticated() {
    const token = localStorage.getItem('ACCESS_TOKEN');
    const authStatus = !!token;
    console.log("Token:", token, "Authenticated:", authStatus);
    return authStatus;
}