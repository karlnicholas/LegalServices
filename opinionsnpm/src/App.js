import React from "react";
import { Link, BrowserRouter as Router, Route } from "react-router-dom";
import http from "./http-common";
import logo from './spring-logo-30.png';

import Home from "./Home";
import Opinions from "./Opinions";
import OpinionsDatesDropdown from "./OpinionsDatesDropdown";


export default class App extends React.Component {
	state = {
		status: false, 
		dates: []
	}
	intervalID;
	componentDidMount() {
		this.getData();
	}
    componentWillUnmount() {
    	clearTimeout(this.intervalID);
    }
    getData = () => {
		http.get('/opinions/status').then(response => {
			var status = Boolean(response.data);
			if ( this.state.status !== status ) {
				this.setState({
					status: status
				})
			};
			if (status) {
				http.get('/opinions/dates').then(response => {
					this.setState({
						dates: response.data
					});
				});
			} else {
				this.intervalID = setTimeout(this.getData.bind(this), 1000);
			}
		});
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
		          <OpinionsDatesDropdown status={this.state.status} dates={this.state.dates}/>
		          </ul>
		      </div>
		    </nav>
	        <Route exact path="/" render={() => <Home status={this.state.status}/>}/>
	        <Route exact path="/opinions/:startDate" component={Opinions} />
	      </Router>
	    </section>
	  );
	}
};
