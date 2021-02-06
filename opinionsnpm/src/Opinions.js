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
	componentDidMount() {
		// Typical usage (don't forget to compare props):
		if (this.props.match.params.startDate != null ) {
			http.get('/opinions/opinions?startDate='+this.props.match.params.startDate).then(response => {
				this.setState({
					opinions: response.data,
					startDate: this.props.match.params.startDate
				});
				
			});
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

