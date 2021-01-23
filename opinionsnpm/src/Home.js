import React from "react";
import http from "./http-common";

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
		  loadedButton = <button type="button" className="btn btn-primary" onClick = {this.handleLoadingClick}>Load Opinions</button>
	  }
    return (
      <div>
      {loadedButton}
      </div>
    );
  }
}
