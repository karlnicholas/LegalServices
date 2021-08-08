
import React, {useState, useEffect} from 'react';
import { setUserSession } from './Utils/Common';
import httplogin from "./httplogin";
import AppNavDropdown from "./AppNavDropdown";
import LogoNav from "./LogoNav";

export default function Login(props) {
    const [loading, setLoading] = useState(false);
    const username = useFormInput('');
    const password = useFormInput('');
    const [error, setError] = useState(null);

    // handle button click of login form
    const handleLogin = () => {
        setError(null);
        setLoading(true);
        httplogin.post('/signin', { username: username.value, password: password.value }).then(response => {
            setLoading(false);
            setUserSession(response.data.token, response.data.username);
            props.history.push('/user');
        }).catch(error => {
            setLoading(false);
            setError("Login attempt failed.");
        });
    }

  return (
      <div className="container">
      <nav className="navbar navbar-expand-lg navbar-light bg-light">
        {LogoNav()}
        <div className="collapse navbar-collapse" id="navbarSupportedContent">
        {AppNavDropdown()}
        </div>
      </nav>
      <div className="row justify-content-center">
          <div className="col-md-8">
              <div className="card">
                  <div className="card-header">Login</div>
                  <div className="card-body">
                      <div className="form-group row">
                          <label for="username" className="col-md-4 col-form-label text-md-right">E-Mail Address</label>
                          <div className="col-md-6">
                              <input type="text" id="username" className="form-control" name="username" required autoFocus {...username} />
                          </div>
                      </div>
                      <div className="form-group row">
                          <label for="password" className="col-md-4 col-form-label text-md-right">Password</label>
                          <div className="col-md-6">
                              <input type="password" id="password" className="form-control" name="password" required {...password} />
                          </div>
                      </div>
                      <div className="col-md-6 offset-md-4">
                          <input type="button" className="btn btn-primary" value={loading ? 'Loading...' : 'Login'} onClick={handleLogin} disabled={loading} />
                          <a href="#" className="btn btn-link">
                              Forgot Your Password?
                          </a>
                      </div>
                  </div>
              </div>
          </div>
      </div>
    </div>
  );
};

const useFormInput = initialValue => {
    const [value, setValue] = useState(initialValue);

    const handleChange = e => {
        setValue(e.target.value);
    }
    return {
        value,
        onChange: handleChange
    }
}