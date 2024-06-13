import React, { useState, useEffect } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import App from "./App";
import Login from "./Login";
import SignUp from "./SignUp";
import { isAuthenticated } from "./service/ApiService";

function Copyright() {
  return (
    <Typography variant="body2" color="textSecondary" align="center">
      {"저작권 "}
      fsoftwareengineer, {new Date().getFullYear()}
      {"."}
    </Typography>
  );
}


function AppRouter() {
  const [isAuth, setIsAuth] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const authStatus = await isAuthenticated();
        console.log("Authentication status:", authStatus); // 로그로 상태 확인
        setIsAuth(authStatus);
      } catch (error) {
        console.error('Authentication check failed:', error);
      } finally {
        setLoading(false); // 로딩 상태는 인증 상태 확인 후에 해제
      }
    };
  
    checkAuth();
  }, []);
  
  if (loading) {
    return <Typography variant="h4" align="center">로딩중...</Typography>;
  }
else{
  return (
    <Router>
      <div>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<SignUp />} />
          <Route path="/" element={isAuth ? <App /> : <Navigate replace to="/login" />} />
        </Routes>
      </div>
      <Box mt={5}>
        <Copyright />
      </Box>
    </Router>
  );
}
}
export default AppRouter;
