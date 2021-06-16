'use strict';
const React = require('react'); 
const client = require('./client');

export class Interested extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			ticker: '',
			action: '',
		};
		this.handleSubmit = this.handleSubmit.bind(this);
		this.handleChange = this.handleChange.bind(this);
		this.handleAction = this.handleAction.bind(this);
	}
	
	handleChange(event) {
      const target = event.target;
      this.setState({
        [target.name]: target.value});
    }

    handleAction(event) {
      const target = event.target;
      this.setState({
        [target.name]: target.value
      });
    }

	handleSubmit(event) {
		if (this.state.action === "Add") {
			client({method: 'POST', path: '/home/addInterested', params: {
                'ticker': this.state.ticker,
              }
            }).done(response => {
              console.log(response);})
         } 
         if (this.state.action === "Delete") {
            client({method: 'POST', path: '/home/deleteInterested', params: {
                'ticker': this.state.ticker,
              }
             }).done(response => {
              console.log(response);
           })
         } 

    }

	render() {
    return (
	<div className= "col">
          <div className="container owned">
            <div className="card stocklist" >
              <div className="card-header">
                Interested
              </div>
	
      <div>
        <form onSubmit={this.handleSubmit}>
          <label>
            Add Interested Stock
          <input
type="text"
            name="ticker"
            onChange={this.handleChange}
            value={this.state.ticker}
          />
          </label>
          <input type="submit" onClick={this.handleAction} name="action" value="Add" />
          <input type="submit" onClick={this.handleAction} name="action" value="Delete" />
        </form>
      </div>
</div>
</div>
</div>
    );
  }
}


export class InterestedItems extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			tickers: [],
			items: '',
			// {} or ''?
		};
	}
	
	tick() {
    client({method: 'GET', path: 'https://sandbox.iexapis.com/v1/stock/market/batch',
    params: {'types': 'price',
    'symbols': this.state.tickers,
    'token' : 'Tpk_18dfe6cebb4f41ffb219b9680f9acaf2'}
    }).then(res => {
      this.setState(state => ({
	       items: res.entity
     }))
    });
  }

  componentDidMount() {
	client({method: 'GET', path: '/home/getInterested'}).then(res => {
	  res.entity.forEach((ob) => this.setState(prevState => ({tickers: [...prevState.tickers, ob]})))
    });
    if (this.state.tickers !== null) {
	this.interval = setInterval(() => this.tick(), 1000); 
	}
  }

  componentWillUnmount() {
    clearInterval(this.interval);
  }

	render() {
    return (
	 <div className= "col">
        <div className="container owned">
<h4>Favourites</h4>
           <div className="card stocklist" >
   {Object.keys(this.state.items).map(key => (
		
          <div className="list-group-item">
               <h6 className="tickers">{key}</h6>
               <h5 className="price">{this.state.items[key].price}</h5>
			<div>
			   <button type="button" className="btn btn-outline-primary">Buy</button>
			</div>
		  </div>
       ))}
</div>
</div>
</div>
    );
  }
}

