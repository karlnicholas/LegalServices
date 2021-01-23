import React, { Component } from "react";
import { Switch, Route, Link } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";

import Home from "./Home";
import Opinions from "./Opinions";

class App extends Component {
  render() {
      return (
	<div>
	    <nav className="navbar navbar-expand-lg navbar-light bg-light">
	    <a className="navbar-brand" href="/"><img src="logo192.png" width="50" height="50" className="d-inline-block align-center" alt="" loading="lazy"/></a>
            <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span className="navbar-toggler-icon"></span>
            </button>
            <div className="collapse navbar-collapse" id="navbarSupportedContent">
            <ul className="navbar-nav mr-auto">
            <li className="nav-item active"><Link to={"/"} className="nav-link">Home</Link></li>
            <li className="nav-item"><Link to={"/opinions"} className="nav-link">Opinions</Link></li>
            <li className="nav-item dropdown">
              	<Link to={"/opinions"} className="nav-link dropdown-toggle" id="dropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"/>
  		<div className="dropdown-menu" aria-labelledby="dropdownMenuLink">
              	    <a className="dropdown-item" href="/">Action</a>
              	    <a className="dropdown-item" href="/">Another action</a>
              	    <a className="dropdown-item" href="/">Something else here</a>
              	 </div>
            </li>
            </ul>
            <ul className="navbar-nav">
            <li className="nav-item dropdown">
            <Link to={"#"} className="nav-link dropdown-toggle" id="navbarDropdown" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Applications</Link>
            <div className="dropdown-menu" aria-labelledby="navbarDropdown">
                <a className="dropdown-item" href="/">Guided Search</a>
                <a className="dropdown-item" href="/">Opinions</a>
          </div>
          </li>
          </ul>
        </div>
        </nav>
        <div className="container mt-3">
          <Switch>
          <Route exact path="/" component={Home} />
            <Route path="/opinions" component={Opinions} />
          </Switch>
        </div>
      </div>
    );
  }
}

export default App;

