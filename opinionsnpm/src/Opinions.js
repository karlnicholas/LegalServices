import React from "react";
import http from "./http-common";
import OpinionView from "./OpinionView";


function LoadingMessage(props) {
	if (props.status) {
		return null;
	}
	return ("Loading opinions ...");
}
function OpinionViews(props) {
	if (!props.status || props.opinions.length === 0) {
		return null;
	}
	const opinionViews = props.opinions.map((op) => <OpinionView key={op.name} opinion={op} />); 
	return opinionViews;
}
export default class Opinions extends React.Component {
	state = {
			status: false, 
			opinions: []
	}
  componentDidMount() {
	  http.get('/opinions/status').then(response => {
		  this.setState({
			  status: response.data
			  });
		  if (this.state.status) {
			  http.get('/opinions/opinions?dateListIndex=0').then(response => {
				  this.setState({
					  opinions: response.data
					  });
				  });
			  }
		  });
	  }
  render() {
	  return (
			  <div>
			  <LoadingMessage status={this.state.status} />
			  <OpinionViews status={this.state.status} opinions={this.state.opinions} />
			  </div>
			  );
	  }
  }
