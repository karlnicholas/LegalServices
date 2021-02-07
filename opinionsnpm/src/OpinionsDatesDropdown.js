import 'bootstrap/dist/css/bootstrap.min.css';
import React from "react";
import { Link } from "react-router-dom";

export default class OpinionsDatesDropdown extends React.Component {
	getDropdown() {
		if ( this.props.dates.length > 0 ) {
	  		return this.props.dates.map((date, index) => (
  		          <Link key={index} className="dropdown-item" to={`/opinions/${date[1]}`}>{date[0]}</Link>
	  		      ))
		} else {
			return ( <a key={1} className="dropdown-item" href='/opinions'>Loading ...</a>); 
		}
	}
	render() {
		return (
    		<li className="nav-item dropdown">
    		<a className="nav-link dropdown-toggle" href="/" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Opinions</a>
    		<div className="dropdown-menu" aria-labelledby="navbarDropdown">
    		{this.getDropdown()}
    		</div>
    		</li>
        );
	}
};

