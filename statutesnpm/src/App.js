import React from "react";
import { Link, BrowserRouter as Router, Route } from "react-router-dom";
import http from "./http-common";
import logo from './spring-logo-30.png';

import Home from "./Home";
import Statutes from "./Statutes";

export default class App extends React.Component {
	state = {
		facetpath: ''
	}
	render() {
	  return (
	    <section className="App">
	      <Router>
		      <nav className="navbar navbar-expand-lg navbar-light bg-light">
	          <Link to="/" ><img className="navbar-brand" src={logo} alt="logo"/></Link>
		      <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
		        <span className="navbar-toggler-icon"></span>
		      </button>
		      <div className="collapse navbar-collapse" id="navbarSupportedContent">
		        <ul className="navbar-nav mr-auto">
		          <li className="nav-item active">
		          <Link to="/" className="nav-link">Home<span className="sr-only">(current)</span></Link>
		          </li>
		          <li className="nav-item">
		          <Link to="/statutes" className="nav-link">Statutes<span className="sr-only"></span></Link>
		          </li>
		          </ul>
		      </div>
		    </nav>
	        <Route exact path="/" render={() => <Home />}/>
	        <Route exact path="/statutes/:facetpath?" component={Statutes} />
	      </Router>
	    </section>
	  );
	}
};

