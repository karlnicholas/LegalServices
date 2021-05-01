import axios from "axios";

export default axios.create({
  baseURL: process.env.OP_SERVICE_URL === undefined ? 'http://localhost:8080/': process.env.OP_SERVICE_URL,  
  headers: {
    "Accept": "application/json"
  }
});