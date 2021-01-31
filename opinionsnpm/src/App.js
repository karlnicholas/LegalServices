import React from "react";
import { Link, BrowserRouter as Router, Route } from "react-router-dom";

import Load from "./Load";
import Home from "./Home";
import Opinions from "./Opinions";
import OpinionsDatesDropdown from "./OpinionsDatesDropdown";


export default class App extends React.Component {
	render() {
	  return (
	    <section className="App">
	      <Router>
		      <nav className="navbar navbar-expand-lg navbar-light bg-light">
	          <Link to="/" className="navbar-brand" >Navbar</Link>
		      <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
		        <span className="navbar-toggler-icon"></span>
		      </button>
		
		      <div className="collapse navbar-collapse" id="navbarSupportedContent">
		        <ul className="navbar-nav mr-auto">
		          <li className="nav-item active">
		          <Link to="/" className="nav-link">Home<span className="sr-only">(current)</span></Link>
		          </li>
		          <li className="nav-item">
		          <Link to="/load" className="nav-link">Load</Link>
		          </li>
		          <OpinionsDatesDropdown />
		          </ul>
		      </div>
		    </nav>
	        <Route exact path="/" component={Home} />
	        <Route exact path="/load" component={Load} />
	        <Route exact path="/opinions/:startDate" component={Opinions} />
	      </Router>
	    </section>
	  );
	}
};

