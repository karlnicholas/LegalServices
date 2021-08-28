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
    const [button, setButton] = useState('logout');

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

    // handle click title change
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
    function changeValue(e) {
        e.persist();
        setProfile(prevProfile => ({ ...prevProfile, [ e.target.name]: e.target.value}));
    }
    // handle submit update
    function handleFormSubmit(e) {
        e.preventDefault();
        if ( button === 'logout') {
            removeUserSession();
            props.history.push('/');
        } else {
            httpUser.post('/profile', profile, {headers: {'Authorization': 'Bearer ' + getToken()}}).then(response => {
                setProfile(response.data);
            }).catch((error) => {
                    console.log(error);
                }
            )
        }
    }

    function showProfile() {
        if (profile != null) {
            return (
                <form onSubmit={handleFormSubmit}>
                    <div className="form-group row">
                        <label htmlFor="staticEmail" className="col-sm-2 col-form-label">Email</label>
                        <div className="col-sm-10">
                            <input type="text" readOnly className="form-control-plaintext" id="staticEmail"
                                   defaultValue={profile.email}/>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="inputFirstname" className="col-sm-2 col-form-label">First</label>
                        <div className="col-sm-10">
                            <input type="text" className="form-control" id="inputFirstname" name="firstName" value={profile.firstName} onChange={changeValue}/>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="inputLastname" className="col-sm-2 col-form-label">Last</label>
                        <div className="col-sm-10">
                            <input type="text" className="form-control" id="inputLastname" name="lastName" value={profile.lastName}  onChange={changeValue}/>
                        </div>
                    </div>
                    <button type="submit" className="btn btn-primary" onClick={e => {e.target.blur(); setButton('logout');}}>Logout</button>
                    <button type="submit" className="btn btn-primary" onClick={e => {e.target.blur(); setButton('update');}}>Update</button>
                </form>
            );
        }
    }

    function tableCell(title, index) {
        if ( title ) {
            return (<td>
                <input className="form-check-input" type="checkbox" id={'inlineFormCheck' + index} onChange={e => handleChangeTitle(e, title)} checked={profile.userTitles.includes(title)}/>
                <label className="form-check-label" htmlFor={'inlineFormCheck'+index}>{title}</label>
            </td>);
        }
    }

    function tableRows()  {
        let trs = [];
        for (let i = 0; i < profile.allTitles.length; i = i + 3) {
            trs.push(profile.allTitles.slice(i, i + 3));
        }
        return trs.map((tr, index) => {
            return (<tr>
                {tableCell(tr[0], index*3)}
                {tableCell(tr[1], index*3+1)}
                {tableCell(tr[2], index*3+2)}
            </tr>);
        })
    }

    function showTitles() {
        if (profile != null) {
            return (
                <div className="table-responsive">
                <table className="table table-borderless">
                    <tbody>
                    {tableRows()}
                    </tbody>
                </table>
                </div>
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

