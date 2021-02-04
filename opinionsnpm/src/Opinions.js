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
/*
	componentDidUpdate(prevProps) {
		console.log("c + p : date" + new Date());
		console.log("c + p : " + this.props.match.params.startDate + " : " + prevProps.match.params.startDate);
		// Typical usage (don't forget to compare props):
		if (this.props.match.params.startDate !== prevProps.match.params.startDate) {
			console.log("updating: " + this.props.match.params.startDate);
			http.get('/opinions/opinions?startDate='+this.props.match.params.startDate).then(response => {
				this.setState({
					opinions: response.data,
					startDate: this.props.match.params.startDate
				});
				
			});
			console.log("updated");
		}
	}
*/	
	componentDidMount() {
		console.log("c : " + this.props.match.params.startDate);
		// Typical usage (don't forget to compare props):
		if (this.props.match.params.startDate != null ) {
			console.log("updating: " + this.props.match.params.startDate);
			http.get('/opinions/opinions?startDate='+this.props.match.params.startDate).then(response => {
				this.setState({
					opinions: response.data,
					startDate: this.props.match.params.startDate
				});
				
			});
			console.log("updated");
		}
	}

	render() {
		return (
				<div>
				{this.state.opinions.map((opinion, index) => (<OpinionView key={index} opinion={opinion}>test</OpinionView>))}
				</div>
		);
	}
};

