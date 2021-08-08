import axios from "axios";

const axiosoptions = (!process.env.NODE_ENV || process.env.NODE_ENV === 'development') ? 
    { baseURL: 'http://localhost:8092/'}
    :
    { baseURL: '/api/user' }
    ;
    axiosoptions.headers = { "Accept": "application/json" }
const httplogin = axios.create(axiosoptions);
export default httplogin;
