import React from "react";
import { signin } from "./service/ApiService";
import { Button, TextField, Grid, Link, Container, Typography } from "@mui/material";

class Login extends React.Component {
    constructor(props) {
        super(props);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit(event) {
        event.preventDefault();
        const data = new FormData(event.target);
        const email = data.get("email");
        const password = data.get("password");

        // ApiService의 signin 메소드를 사용해 로그인
        signin({ email: email, password: password }).then((response) => {
            console.log("로그인 성공");
            window.location.href = "/";
        }).catch((error) => {
            console.error("로그인 실패", error);
        });
    }

    render() {
        return (
            <Container component="main" maxWidth="xs" style={{ marginTop: "8%" }}>
                <Grid container spacing={2} justifyContent="center">
                    <Grid item xs={12}>
                        <Typography component="h1" variant="h5" align="center">
                            로그인
                        </Typography>
                    </Grid>
                    <form noValidate onSubmit={this.handleSubmit}>
                        <Grid container spacing={2}>
                            <Grid item xs={12}>
                                <TextField
                                    variant="outlined"
                                    required
                                    fullWidth
                                    id="email"
                                    label="이메일 주소"
                                    name="email"
                                    autoComplete="email"
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <TextField
                                    variant="outlined"
                                    required
                                    fullWidth
                                    id="password"
                                    label="패스워드"
                                    name="password"
                                    type="password"
                                    autoComplete="current-password"
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <Button
                                    type="submit"
                                    fullWidth
                                    variant="contained"
                                    color="primary"
                                >
                                    로그인
                                </Button>
                            </Grid>
                            <Grid item xs={12}>
                                <Link href="/signup" variant="body2">
                                    계정이 없습니까? 여기서 가입하세요.
                                </Link>
                            </Grid>
                        </Grid>
                    </form>
                </Grid>
            </Container>
        );
    }
}

export default Login;
