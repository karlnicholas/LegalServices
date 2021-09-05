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

    function changeOptout(e) {
        e.persist();
        setProfile(prevProfile => ({ ...prevProfile, optout: !prevProfile.optout}));
    }

    function showProfile() {
        if (profile != null) {
            return (
                <form onSubmit={handleFormSubmit}>
                    <div className="form-group row">
                        <label htmlFor="staticEmail" className="col-sm-2 col-form-label">Email</label>
                        <div className="col-sm-10">
                            <input type="text" readOnly className="form-control-plaintext" id="staticEmail" defaultValue={profile.email}/>
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
                            <input type="text" className="form-control" id="inputLastname" name="lastName" value={profile.lastName} onChange={changeValue}/>
                        </div>
                    </div>
                    <div className="form-group form-check">
                        <input className="form-check-input" type="checkbox" id={'inlineFormCheckOptout'} onChange={changeOptout} checked={profile.optout}/>
                        <label className="form-check-label" htmlFor={'inlineFormCheckOptout'}>Notification Optout</label>
                    </div>
                    <div className="row justify-content-start">
                        <span className="col-2"><button type="submit" className="btn btn-primary" onClick={e => {e.target.blur(); setButton('update');}}>Update</button></span>
                        <span className="col-2"><button type="submit" className="btn btn-primary" onClick={e => {e.target.blur(); setButton('logout');}}>Logout</button></span>
                    </div>
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
                <div className="table-responsive container">
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

    function showContent(active, bodyFunction) {
        return (
            <div>
            <ul className="nav nav-tabs">
                <li className="nav-item" style={{cursor: 'pointer'}}>
                    <span className={active === 'profile' ? "active nav-link": "nav-link"} onClick={setTabProfile}>Profile</span>
                </li>
                <li className="nav-item" style={{cursor: 'pointer'}}>
                    <span className={active === 'titles' ? "active nav-link": "nav-link"} onClick={setTabTitles}>Titles</span>
                </li>
            </ul>
            {bodyFunction()}
            </div>
        );
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
            {showContent(tabstate, tabstate === 'titles' ? showTitles : showProfile)}
        </div>
    );
};

