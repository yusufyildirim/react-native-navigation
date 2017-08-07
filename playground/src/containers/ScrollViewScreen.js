import _ from 'lodash';
import React, { Component } from 'react';
import {
  StyleSheet,
  ScrollView,
  View,
  Text,
  Button
} from 'react-native';

import Navigation from 'react-native-navigation';

class ScrollViewScreen extends Component {
  static navigationOptions = {
    topBarTranslucent: false
  }

  constructor(props) {
    super(props);
    this.state = {
      topBarHideOnScroll: false
    };
    this.onClickToggleTopBarHideOnScroll = this.onClickToggleTopBarHideOnScroll.bind(this);
  }
  
  render() {
    return (
      <ScrollView testID="scrollView" contentContainerStyle={styles.contentContainer}>
        <View>
          <Button title="Toggle Top Bar Hide On Scroll" onPress={this.onClickToggleTopBarHideOnScroll} />
        </View>
      </ScrollView>
    );
  }

  onClickToggleTopBarHideOnScroll() {
    Navigation.setOptions(this.props.containerId, {
      topBarHideOnScroll: !this.state.topBarHideOnScroll
    });
  }
}

const styles = StyleSheet.create({
  contentContainer: {
    paddingVertical: 20,
    alignItems: 'center',
    height: 1000
  }
});
export default ScrollViewScreen;
