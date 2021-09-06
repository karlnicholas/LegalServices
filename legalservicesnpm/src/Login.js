
import React, {useState} from 'react';
import {setUserSession} from './Utils/Common';
import AppNavDropdown from "./AppNavDropdown";
import LogoNav from "./LogoNav";
import httpUser from "./httpUser";

export default function Login(props) {
    const username = useFormInput('');
    const password = useFormInput('');
    const [error, setError] = useState(null);

    // handle submit update
    function handleFormSubmit(e) {
        e.preventDefault();
        setError(null);
        httpUser.post('/signin', { username: username.value, password: password.value }).then(response => {
            setUserSession(response.data.token, response.data.username);
            props.history.push('/user');
        }).catch(error => {
            setError("Login attempt failed.");
        });
    }

    function showError() {
        if ( error ) {
            return (
                <div className="form-group row">
                    {error}
                </div>
            )
        }
    }

  return (
      <div className="container">
      <nav className="navbar navbar-expand-lg navbar-light bg-light">
        {LogoNav()}
        <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent"
              aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
          <span className="navbar-toggler-icon"></span>
        </button>
        <div className="collapse navbar-collapse" id="navbarSupportedContent">
        {AppNavDropdown()}
        </div>
      </nav>
          <div className="row justify-content-center">
              <div className="col-md-8">
                  <form onSubmit={handleFormSubmit}>
                      {showError()}
                      <div className="form-group row">
                          <label htmlFor="inputEmail" className="col-sm-2 col-form-label">Email</label>
                          <div className="col-sm-10">
                              <input type="email" id="username" className="form-control" name="username" required autoFocus {...username} />
                          </div>
                      </div>
                      <div className="form-group row">
                          <label htmlFor="inputPassword" className="col-sm-2 col-form-label">Password</label>
                          <div className="col-sm-10">
                              <input type="password" id="password" className="form-control" name="password" required {...password} />
                          </div>
                      </div>
                      <div className="form-group row">
                          <div className="col-sm-2">
                              <button type="submit" className="btn btn-primary">Signin</button>
                          </div>
                          <div className="col-sm-2">
                              <button type="button" className="btn btn-primary"  onClick={(e) => {e.preventDefault(); window.location.href='/register';}}>Register</button>
                          </div>
                      </div>
                  </form>
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