import React from "react";
import http from "./http-common";
import Container from "react-bootstrap/Container";
import OpinionView from "./OpinionView";

export default class Opinions extends React.Component {
  state = {
	  ready: false, 
	  opinions: []
  }
  componentDidMount() {
    http.get('/opinions/status').then(response => {
      this.setState({
        ready: response.data
      });
    });
    if (this.state.ready) {
        http.get('/opinions/opinions?dateListIndex=0').then(response => {
	        this.setState({
	        	opinions: response.data
	        });
        });
    }
  }
  render() {
	  const ready = this.state.ready;
	  const opinions = this.state.opinions;
	  let readyStatus;
	  if ( !ready) {
		  readyStatus = "Loading opinions ...";
	  }
    return (
      <Container>
      {readyStatus}
      {opinions.map((opinion) => (<OpinionView></OpinionView>))}
      </Container>
    );
  }
}
