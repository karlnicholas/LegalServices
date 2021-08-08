
import React, {useState, useEffect} from 'react';
import {getToken, getUser, removeUserSession} from './Utils/Common';
import httpUser from "./httpUser";
import AppNavDropdown from "./AppNavDropdown";
import LogoNav from "./LogoNav";

export default function User(props) {
  const user = getUser();
  const [profile, setProfile] = useState(null);

  useEffect(() => {
    if ( user == null ) {
      props.history.push('/login');

    } else {
        httpUser.get('/profile', {headers: {'Authorization': 'Bearer ' + getToken()}}).then(response => {
        setProfile(response.data);
      })
      .catch((error) => {
        console.log(error);
      }
    )}
  }, []);

  // handle click event of logout button
  const handleLogout = () => {
    removeUserSession();
    props.history.push('/');
  }

  function showProfile() {
    if ( profile != null ) {
      return (
          <div>
            <p>Email: {profile.email}</p>
            <p>First: {profile.firstName}</p>
            <p>Last: {profile.lastName}</p>
          </div>
      );
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
      User Stuff
        {showProfile()}
    </div>
  );
};

