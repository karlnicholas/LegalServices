import axios from "axios";

const axiosoptions = (!process.env.NODE_ENV || process.env.NODE_ENV === 'development') ? 
    { baseURL: 'http://localhost:8091/', headers: { "Accept": "application/json" }}
    :
    { headers: { "Accept": "application/json" }}
    ;

const httpstatutes = axios.create(axiosoptions);
export default httpstatutes;
