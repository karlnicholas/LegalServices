import React from "react";
import http from "./http-common";
import Container from "react-bootstrap/Container";
import BootstrapTable from "react-bootstrap-table-next";

export default class Opinions extends React.Component {
  state = {
	  ready: false
  }
  componentDidMount() {
    http.get('/opinions/ready').then(response => {
      this.setState({
        ready: response.data
      });
    });
  }
  render() {
    return (
      <Container>
      	Hello World: {this.state.ready.toString()}
      </Container>
    );
  }
}
