import 'bootstrap/dist/css/bootstrap.min.css';
import React from "react";
import { Link } from "react-router-dom";
import http from "./http-common";

export default class OpinionsDatesDropdown extends React.Component {
	state = {
		status: false, 
		dates: []
	}
	componentDidMount() {
		http.get('/opinions/status').then(response => {
			this.setState({
				status: response.data
			});
			if (this.state.status) {
				http.get('/opinions/dates').then(response => {
					this.setState({
						dates: response.data
					});
				});
			}
		});
	}
	render() {
		const dates = this.state.dates;
		  return (
	          <li className="nav-item dropdown">
	          <a className="nav-link dropdown-toggle" href="/" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
	            Opinions
	          </a>
	          <div className="dropdown-menu" aria-labelledby="navbarDropdown">
	      		{dates.map((date, index) => (
			        <h5 key={index}>
			          <a className="dropdown-item" href={`/opinions/${date[1]}`}>{date[0]}</a>
			        </h5>
			      ))}
	          </div>
	          </li>
		  );
	}
};

