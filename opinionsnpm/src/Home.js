import React from "react";
import http from "./http-common";
import Container from "react-bootstrap/Container";
import Button from "react-bootstrap/Button";

export default class Home extends React.Component {
  constructor(props) {
   super(props);
   this.handleLoadingClick = this.handleLoadingClick.bind(this);
   this.state = {
     ready: false
   };
  }
  handleLoadingClick() {
    if (!this.state.opinionsLoading) {
    	http.get('/home/load').then(response => {});
    }
    this.setState({ready: true});
  }
  componentDidMount() {
    http.get('/home/ready').then(response => {
      this.setState({
    	  ready: response.data
      });
    });
  }

  render() {
	  const ready = this.state.ready;
	  let loadedButton;
	  if ( !ready) {
		  loadedButton = <Button variant="primary" onClick = {this.handleLoadingClick}>Load Opinions</Button>
	  }
    return (
      <Container>
      {loadedButton}
      </Container>
    );
  }
}
