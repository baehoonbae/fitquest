import axios from "axios";

const http = axios.create({
  // localhost 대신 실제 IP 주소 사용
  baseURL: "http://localhost:8097/fitquest/api",
  withCredentials: true,
});

export default http;
