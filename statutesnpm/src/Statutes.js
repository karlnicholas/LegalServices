import React from "react";
import http from "./http-common";
import StatutesRecurse from "./StatutesRecurse";

export default class Statutes extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			entries: [], 
			facetpath: '',
			hiddenFrag: false, 
		}
	}
	fetchStatutes(facetpath) {
		// Typical usage (don't forget to compare props):
		return http.get('/api')
		.then(response => {
			this.setState({entries: response.data.entries, facetpath: facetpath});
		});
	}
	
	componentDidUpdate(prevProps, prevState) {
		if ( this.props.match.params.facetpath !== this.state.facetpath ) {
			this.fetchStatutes(this.props.match.params.facetpath);
		} 
	}

	componentDidMount() {
		this.fetchStatutes(this.props.match.params.facetpath);
	}

	render() {
		if ( this.state.entries.length > 0 ) {
			return (
					<div>
					<StatutesRecurse entries={this.state.entries} index={0}/>
					</div>
			)
		} else {
			return (<div></div>);
		}
	}
}
