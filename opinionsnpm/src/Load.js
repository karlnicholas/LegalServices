import React from "react";
import http from "./http-common";

function LoadMessage(props) {
	if (props.status) {
		return ("Loaded");
	}
	return ("Loading opinions ...");
}

export default class Load extends React.Component {
	state = {
			status: false
		}
		componentDidMount() {
			http.get('/opinions/status').then(response => {
				this.setState({
					status: response.data
				});
			});
		}
	render() {
		return (<LoadMessage status={this.state.status} />);
	}
}
