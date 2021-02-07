import React from "react";

export default class Home extends React.Component {
	render() {
		let statusVal = "Loading opinions ...";
		if (this.props.status) {
			statusVal = "Loaded";
		}
		return (statusVal);
	}
}
