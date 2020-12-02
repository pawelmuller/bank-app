//import React, { component } from 'react';
//import logo from './logo.svg';
import './App.css';

const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');

class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = {bankClients: []};
  }

  componentDidMount() {
    client({method: 'GET', path: '/api/clients'}).done(response => {
      this.setState({bankClients: response.entity._embedded.bankClients});
    });
  }

  render() {
    return (
        <BankClientsList bankClients={this.state.bankClients}/>
    )
  }
}

class BankClientsList extends React.Component{
  render() {
    const bankClients = this.props.bankClients.map(bankClient =>
        <BankClients key={bankClient._links.self.href} bankClient={bankClient}/>
    );
    return (
        <table>
          <tbody>
          <tr>
            <th>Name</th>
            <th>Surname</th>
          </tr>
          {bankClients}
          </tbody>
        </table>
    )
  }
}


class BankClients extends React.Component{
  render() {
    return (
        <tr>
          <td>{this.props.bankClient.name}</td>
          <td>{this.props.bankClient.surname}</td>
        </tr>
    )
  }
}

ReactDOM.render(
    <App />,
    document.getElementById('react')
)

/*
function App() {
  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.js</code> and save to reload.<br/>Elo yolo itd
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>
    </div>
  );
}
*/
export default App;
