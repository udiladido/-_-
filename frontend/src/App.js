import React from 'react';
import Todo from './Todo';
import AddTodo from './AddTodo';
import { Paper, List, Container, Typography, Grid, Button, AppBar, Toolbar, IconButton } from "@mui/material";
import ArrowBack from '@mui/icons-material/ArrowBack';
import ArrowForward from '@mui/icons-material/ArrowForward';
import DarkModeIcon from '@mui/icons-material/DarkMode'; // 다크모드 아이콘 추가
import './App.css';

import { call, signout, isAuthenticated } from './service/ApiService';

const clockLogo = require('./free-icon-clock-98170.png');
const locationLogo = require('./free-icon-location-4874744.png');

class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      items: [],
      currentTime: new Date(),
      address: '', 
      loading: true,
      page: 0,
      totalPages: 0,
      startPage: 0,
      endPage: 0,
    };
  }

  componentDidMount() {
    if (isAuthenticated()) {
      this.loadTodos(0);
    } else {
      this.setState({ loading: false });
    }

    this.timeInterval = setInterval(() => {
      this.setState({ currentTime: new Date() });
    }, 1000);

    this.getLocation();
  }

  componentWillUnmount() {
    clearInterval(this.timeInterval);
  }

  getLocation = () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        this.fetchLocationDetails,
        this.handleLocationError,
        { timeout: 10000 } // 10초 내에 위치 정보를 가져오지 못하면 에러 처리
      );
    } else {
      alert("Geolocation is not supported by this browser.");
      this.setState({ address: 'Geolocation not supported.' });
    }
  }
  fetchLocationDetails = (position) => {
    const { latitude, longitude } = position.coords;
    this.fetchAddress(latitude, longitude);
  }

  fetchAddress = async (latitude, longitude) => {
    const apiKey = 'AIzaSyCRQ9NdK-XhglCFeeAmd7XQFr2Af958O1g'; 
    const url = `https://maps.googleapis.com/maps/api/geocode/json?latlng=${latitude},${longitude}&key=${apiKey}`;
    
    try {
      const response = await fetch(url);
      const data = await response.json();
      if (response.ok) {
        const address = data.results[0]?.formatted_address;
        console.log("Address:", address); // 주소 정보 출력
        this.setState({ address });
      } else {
        throw new Error('Failed to fetch address');
      }
    } catch (error) {
      console.error("Error fetching address:", error);
    }
  }

  handleLocationError = (error) => {
    let errorMessage = 'Unable to fetch location.';
    switch (error.code) {
      case error.PERMISSION_DENIED:
        errorMessage = "User denied the request for Geolocation.";
        break;
      case error.POSITION_UNAVAILABLE:
        errorMessage = "Location information is unavailable.";
        break;
      case error.TIMEOUT:
        errorMessage = "The request to get user location timed out.";
        break;
      case error.UNKNOWN_ERROR:
        errorMessage = "An unknown error occurred.";
        break;
    }
    console.error("Error Code = " + error.code + " - " + error.message);
    this.setState({ address: errorMessage });
  }

  loadTodos = (page) => {
    this.setState({ loading: true });
    call(`/todo/page?page=${page}&size=3`, "GET", null).then((response) => {
      this.setState({ 
        items: response.todoPage.content,
        loading: false,
        page: response.todoPage.number,
        totalPages: response.todoPage.totalPages,
        startPage: response.startPage,
        endPage: response.endPage,
      });
    }).catch((error) => {
      console.error("로딩 실패", error);
      this.setState({ loading: false });
    });
  }


  add = (item) => {
    call("/todo", "POST", item).then((response) => {
      this.loadTodos(0);
    });
  }

  delete = (item) => {
    call("/todo", "DELETE", item).then((response) => {
      this.setState({ page: 0 }, () => {
        this.loadTodos(0);
      });
    });
  }

  deleteAll = () => {
    console.log("전체 삭제 버튼 클릭됨");
    call("/todo/all", "DELETE").then((response) => {
      console.log(response);
      console.log("전체 삭제 성공");
      this.setState({ items: [], page: 0, totalPages: 0 });
    }).catch((error) => {
      console.error("전체 삭제 실패", error);
    });
  }

  update = (item) => {
    call("/todo", "PUT", item).then((response) => {
      this.loadTodos(this.state.page);
    }).catch((error) => {
      console.error("업데이트 실패", error);
    });
  }



  deleteSelectedItems = () => {
    const itemsToDelete = this.state.items.filter(item => item.done);
    const deletePromises = itemsToDelete.map(item =>
      call("/todo", "DELETE", item)
    );
  
    Promise.all(deletePromises)
      .then(() => {
        console.log("All items deleted successfully");
        this.loadTodos(); // 서버에서 최신 상태를 다시 로드
      })
      .catch(error => {
        console.error("Error deleting items:", error);
      });
  };

  
  handlePrevPage = () => {
    if (this.state.page > 0) {
      this.loadTodos(this.state.page - 1);
    }
  }

  handleNextPage = () => {
    if (this.state.page < this.state.totalPages - 1) {
      this.loadTodos(this.state.page + 1);
    }
  }


  toggleDarkMode = () => {
    this.setState((prevState) => ({ darkMode: !prevState.darkMode }));
  }

  render() {
    const { items, loading, currentTime, address, page, totalPages, darkMode} = this.state;

    if (loading) {
      return <Typography variant="h1">로딩중...</Typography>;
    }

    const todoItems = items.length > 0 && (
      <Paper style={{ margin: 16 }}>
        <List>
          {items.map((item) => (
            <Todo item={item} key={item.id} delete={this.delete} onSelectChange={this.handleSelectChange} update={this.update} />
          ))}
        </List>
      </Paper>
    );

    const navigationBar = (
      <AppBar position="static" style={{ backgroundColor: '#000000' }}>
        <Toolbar>
          <Grid container justifyContent="space-between">
            <Typography variant="h6" component="div">오늘의 할일</Typography>
            <DarkModeIcon 
            onClick={this.toggleDarkMode}
            style={{ cursor: 'pointer', position: 'absolute', right: 100, top: 20 }}
            />
            <Button color="inherit" onClick={signout}>Logout</Button>
          </Grid>
        </Toolbar>
      </AppBar>
    );

    const appClassName = darkMode ? 'App dark-mode' : 'App';

    return (
      <div className={appClassName}>
         <div className='App'>
        {navigationBar}
        <Container maxWidth="md">
          <Grid container direction="column" alignItems="center" spacing={2} style={{ margin: '16px 0' }}>
            <Grid item container alignItems="center">
              <img src={clockLogo} alt="Clock Logo" style={{ width: 30, height: 30, marginRight: 8 }} />
              <Typography variant="h5">현재 시간: {currentTime.toLocaleTimeString()}</Typography>
            </Grid>
            <Grid item container alignItems="center">
              <img src={locationLogo} alt="Location Logo" style={{ width: 30, height: 30, marginRight: 8 }} />
              <Typography variant="subtitle1">현재 위치: {address}</Typography>
            </Grid>
          </Grid>
          <AddTodo add={this.add} />
          <div className='TodoList'>{todoItems}</div>
          <Button variant="contained" color="secondary" onClick={this.deleteAll} style={{ margin: '16px 0' }}>
            전체 삭제
          </Button>
          <Button
          variant="contained"
          color="secondary"
          onClick={this.deleteSelectedItems}
          disabled={!this.state.items.some(item => item.done) || this.state.loading}
>
             선택 삭제
            </Button>
          <Grid container justifyContent="center" style={{ marginTop: 16 }}>
            <IconButton onClick={this.handlePrevPage} disabled={page === 0}>
              <ArrowBack />
            </IconButton>
            <Typography variant="body2" style={{ display: 'inline', margin: '0 16px' }}>
              Page {page + 1} of {totalPages}
            </Typography>
            <IconButton onClick={this.handleNextPage} disabled={page >= totalPages - 1}>
              <ArrowForward />
            </IconButton>
          </Grid>
          </Container>
        </div>
      </div>
    );
  }
}

export default App;
