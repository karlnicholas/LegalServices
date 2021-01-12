import React, { useState, useEffect } from "react";
import http from "./http-common";
import Container from "react-bootstrap/Container";
import Button from "react-bootstrap/Button";
import BootstrapTable from "react-bootstrap-table-next";

export default class Home extends React.Component {
  constructor(props) {
   super(props);
   this.handleLoadingClick = this.handleLoadingClick.bind(this);
   this.state = {
     opinionsLoading: false, 
     opinionsLoaded: false
   };
  }
  handleLoadingClick() {
    if (!this.state.opinionsLoading) {
    	http.get('/opinions/load').then(response => {});
    }
    this.setState({opinionsLoading: true});
  }
  componentDidMount() {
    http.get('/opinions/ready').then(response => {
      this.setState({
    	  opinionsLoaded: response.data
      });
    });
  }

  render() {
	  const opinionsLoaded = this.state.opinionsLoaded;
	  let loadedButton;
	  if ( !this.state.opinionsLoaded && !this.state.opinionsLoading) {
		  loadedButton = <Button variant="primary" onClick = {this.handleLoadingClick}>Load Opinions</Button>
	  } else if ( this.state.opinionsLoading ) {
		  loadedButton = <Button variant="primary" disabled>Opinions Loading</Button>
	  } else {
		  loadedButton = <Button variant="primary" disabled>Opinions Loaded</Button>
	  }
    return (
      <Container>
      {loadedButton}
      </Container>
    );
  }
}
