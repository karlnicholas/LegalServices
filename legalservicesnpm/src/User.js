import React, {useState, useEffect} from 'react';
import { useHistory } from "react-router-dom";
import {getToken, getUser, removeUserSession} from './Utils/Common';
import httpUser from "./httpUser";
import AppNavDropdown from "./AppNavDropdown";
import LogoNav from "./LogoNav";

export default function User(props) {
    const user = getUser();
    const history = useHistory();
    const [tabstate, setTabstate] = useState('profile');
    const [profile, setProfile] = useState(null);

    useEffect(() => {
        if (user == null) {
            history.push('/login');
        } else {
            httpUser.get('/profile', {headers: {'Authorization': 'Bearer ' + getToken()}}).then(response => {
                setProfile(response.data);
            }).catch((error) => {
                    console.log(error);
                }
            )
        }
    }, [user, history]);

    // handle click event of logout button
    const handleLogout = () => {
        removeUserSession();
        props.history.push('/');
    }

    function updateProfileTitles(title) {
        if ( profile.userTitles.includes(title)) {
            for (var i = profile.userTitles.length - 1; i >= 0; i--) {
                if (profile.userTitles[i] === title) {
                    profile.userTitles.splice(i, 1);
                }
            }
        } else {
            profile.userTitles.push(title);
        }
    }

    // handle click event of logout button
    function handleChangeTitle(e, title) {
        e.preventDefault();
        updateProfileTitles(title);
        httpUser.post('/profile', profile, {headers: {'Authorization': 'Bearer ' + getToken()}}).then(response => {
            setProfile(response.data);
        }).catch((error) => {
                console.log(error);
            }
        )
    }

    function showProfile() {
        if (profile != null) {
            return (
                <div>
                    <p>Email: {profile.email}</p>
                    <p>First: {profile.firstName}</p>
                    <p>Last: {profile.lastName}</p>
                    <button type="button" className="btn btn-primary" onClick={handleLogout}>Logout</button>
                </div>
            );
        }
    }

    function showTitles() {
        if (profile != null) {
            return (
                <ul className="list-group">
                    {profile.allTitles.map(( title, index ) => {
                        return (<li key={'titlekey'+index} className="list-group-item">
                            <input className="form-check-input" type="checkbox" id={'inlineFormCheck'+index} onChange={e => handleChangeTitle(e, title)} checked={profile.userTitles.includes(title)}/>
                            <label className="form-check-label" htmlFor={'inlineFormCheck'+index}>
                                {title}
                            </label>
                        </li>);
                    })}
                </ul>
            );
        }
    }

    function setTabProfile() {
        setTabstate('profile');
    }

    function setTabTitles() {
        setTabstate('titles');
    }

    function setTabSettings() {
        setTabstate('settings');
    }

    function showTabs() {
        if (tabstate === 'settings') {
            return (
                <ul className="nav nav-tabs">
                    <li className="nav-item" style={{cursor: 'pointer'}}>
                        <span className="nav-link" onClick={setTabProfile}>Profile</span>
                    </li>
                    <li className="nav-item" style={{cursor: 'pointer'}}>
                        <span className="nav-link" onClick={setTabTitles}>Titles</span>
                    </li>
                    <li className="nav-item" style={{cursor: 'pointer'}}>
                        <span className="nav-link active" onClick={setTabSettings}>Settings</span>
                    </li>
                </ul>
            );
        } else if (tabstate === 'titles') {
            return (
                <div>
                <ul className="nav nav-tabs">
                    <li className="nav-item" style={{cursor: 'pointer'}}>
                        <span className="nav-link" onClick={setTabProfile}>Profile</span>
                    </li>
                    <li className="nav-item" style={{cursor: 'pointer'}}>
                        <span className="nav-link active" onClick={setTabTitles}>Titles</span>
                    </li>
                    <li className="nav-item" style={{cursor: 'pointer'}}>
                        <span className="nav-link" onClick={setTabSettings}>Settings</span>
                    </li>
                </ul>
                {showTitles()}
                </div>
            );
        } else {
            return (
                <div>
                    <ul className="nav nav-tabs">
                        <li className="nav-item" style={{cursor: 'pointer'}}>
                            <span className="nav-link active" onClick={setTabProfile}>Profile</span>
                        </li>
                        <li className="nav-item" style={{cursor: 'pointer'}}>
                            <span className="nav-link" onClick={setTabTitles}>Titles</span>
                        </li>
                        <li className="nav-item" style={{cursor: 'pointer'}}>
                            <span className="nav-link" onClick={setTabSettings}>Settings</span>
                        </li>
                    </ul>
                    {showProfile()}
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
            {showTabs()}
        </div>
    );
};

