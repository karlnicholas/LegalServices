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
	        <Route exact path="/" render={() => <Home />}/>
	        <Route exact path="/statutes/:facetpath?" component={Statutes} />
	      </Router>
	    </section>
	  );
	}
};

