import React from "react";
import http from "./http-common";
import OpinionView from "./OpinionView";
	
export default class Opinions extends React.Component {
	constructor(props) {
		super(props);
		this.state = {opinions: [], startDate: ''}
	}
	fetchOpinions(startDate) {
		// Typical usage (don't forget to compare props):
		return http.get('/opinionviews/cases/'+startDate)
		.then(response => {
			this.setState({opinions: response.data, startDate: startDate});
		});
	}
	
	componentDidUpdate(prevProps, prevState) {
		if ( this.props.match.params.startDate !== this.state.startDate ) {
			this.fetchOpinions(this.props.match.params.startDate);
		} 
	}

	componentDidMount() {
		this.fetchOpinions(this.props.match.params.startDate);
	}

	render() {
		return (
			<div>
			{this.state.opinions.map((opinion, index) => (<OpinionView key={index} opinion={opinion}>test</OpinionView>))}
			</div>
		);
	}
};

