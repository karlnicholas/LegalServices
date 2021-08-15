
import React, {useState} from 'react';
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
    const handleLogin = (e) => {
        e.preventDefault();
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
        <div className="collapse navbar-collapse" id="navbarSupportedContent">
        {AppNavDropdown()}
        </div>
      </nav>
          <div className="row justify-content-center">
              <div className="col-md-8">
                  <form onSubmit={handleLogin}>
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
                          <div className="col-sm-10">
                              <button type="submit" className="btn btn-primary">{loading ? 'Loading ...':'Signin'}</button>
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