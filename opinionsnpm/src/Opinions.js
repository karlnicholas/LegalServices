import 'bootstrap/dist/css/bootstrap.min.css';
import React from "react";
import http from "./http-common";
import OpinionView from "./OpinionView";
	
export default class Opinions extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			startDate: '', 
			opinions: []
		};
	}

	componentDidUpdate(prevProps) {
		// Typical usage (don't forget to compare props):
		if (this.props.match.params.startDate !== prevProps.match.params.startDate) {
			let startDate = this.props.match.params.startDate;
			http.get('/opinions/opinions?startDate='+startDate).then(response => {
				this.setState({
					opinions: response.data
				});
			});
			this.setState({startDate: startDate});
			console.log("updated");
		}
	}
	
    render() {
    	const opinions = this.state.opinions;
		return (
				<div>
				{opinions.map((opinion, index) => (<OpinionView key={index} opinion={opinion}>test</OpinionView>))}
				</div>
		);
	}
};

