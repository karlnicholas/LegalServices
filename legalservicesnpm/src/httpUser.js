import axios from "axios";

const axiosoptions = (!process.env.NODE_ENV || process.env.NODE_ENV === 'development') ? 
    { baseURL: 'http://localhost:8093/api/user'}
    :
    { baseURL: '/api/user'}
    ;
    axiosoptions.headers = {"Accept": "application/json"};

const httpUser = axios.create(axiosoptions);
export default httpUser;
