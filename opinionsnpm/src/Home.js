import React from "react";
import http from "./http-common";

export default class Home extends React.Component {
	testUpdate() {
    	console.log("testUpdate()");
		http.get('/home/testUpdate');
	}
	render() {
		let statusVal = "Loading opinions ...";
		if (this.props.status) {
			statusVal = "Loaded";
		}
		return (<div>
		{statusVal}
		<p><button type="button" className="btn btn-primary" onClick={this.testUpdate}>Load New Opinions</button></p>
		</div>);
	}
}
