const React = require('react');
const { Component } = require('react');

const { View, Text, Button, Platform, StatusBar } = require('react-native');

const { Navigation } = require('react-native-navigation');
const testIDs = require('../testIDs');

const BUTTON_ONE = 'buttonOne';
const BUTTON_TWO = 'buttonTwo';
const CUSTOM_BUTTON = 'customButton';
const CUSTOM_BUTTON2 = 'customButton2';
const BUTTON_LEFT = 'buttonLeft';
const FAB = 'fab';

class OptionsScreen extends Component {
  static get options() {
    return {
      statusBar: {
        style: 'dark',
        backgroundColor: '#EDEDED'
      },
      topBar: {
        title: {
          text: 'Static Title',
          color: 'black',
          fontSize: 16,
          alignment: 'center',
          fontFamily: 'HelveticaNeue-Italic',
          largeTitle: false
        },
        subtitle: {
          text: 'Static Subtitle',
          color: 'red',
          fontFamily: 'HelveticaNeue-Italic',
          alignment: 'center'
        },
        background: {
          component: {
            name: 'TopBarBackground',
            passProps: {
              color: '#bbdefb'
            }
          }
        },
        ...Platform.select({
          android: { drawBehind: true },
          ios: { drawBehind: false, }
        }),
        visible: true,
        testID: testIDs.TOP_BAR_ELEMENT,
        rightButtons: [
          // {
          //   id: CUSTOM_BUTTON,
          //   testID: CUSTOM_BUTTON,
          //   component: 'CustomTextButton'
          // },
          {
            id: CUSTOM_BUTTON2,
            testID: CUSTOM_BUTTON2,
            component: {
              name: 'CustomRoundedButton'
            }
          },
          {
            id: BUTTON_ONE,
            testID: BUTTON_ONE,
            title: 'One',
            fontFamily: 'HelveticaNeue-Italic',
            fontSize: 28,
            color: 'red'
          }
        ],
        leftButtons: [{
          id: BUTTON_LEFT,
          testID: BUTTON_LEFT,
          icon: require('../../img/navicon_add.png'),
          title: 'Left',
          color: 'purple'
        }]
      },
      fab: {
        id: FAB,
        backgroundColor: 'orange',
        clickColor: 'orange',
        rippleColor: 'red',
        alignHorizontally: 'left',
        actions: [
          {
            id: 'fab1',
            backgroundColor: 'blue',
            clickColor: 'blue',
            rippleColor: 'aquamarine',
          },
          {
            id: 'fab2',
            backgroundColor: 'blueviolet',
            clickColor: 'blueviolet',
            size: 'mini',
            rippleColor: 'aquamarine',
          }
        ]
      }
    };
  }

  render() {
    return (
      <View style={styles.root}>
        <View style={{ width: 2, height: 2, backgroundColor: 'red', alignSelf: 'center' }} />
        <Text style={styles.h1} testID={testIDs.OPTIONS_SCREEN_HEADER}>{`Options Screen`}</Text>
        <Button title='Dynamic Options' testID={testIDs.DYNAMIC_OPTIONS_BUTTON} onPress={this.onClickDynamicOptions} />
        <Button title='Show Top Bar' testID={testIDs.SHOW_TOP_BAR_BUTTON} onPress={this.onClickShowTopBar} />
        <Button title='Hide Top Bar' testID={testIDs.HIDE_TOP_BAR_BUTTON} onPress={this.onClickHideTopBar} />
        <Button title='Top Bar Transparent' onPress={this.onClickTopBarTransparent} />
        <Button title='Top Bar Opaque' onPress={this.onClickTopBarOpaque} />
        <Button title='scrollView Screen' testID={testIDs.SCROLLVIEW_SCREEN_BUTTON} onPress={this.onClickScrollViewScreen} />
        <Button title='Custom Transition' testID={testIDs.CUSTOM_TRANSITION_BUTTON} onPress={this.onClickCustomTranstition} />
        {Platform.OS === 'android' ? <Button title='Hide fab' testID={testIDs.HIDE_FAB} onPress={this.onClickFab} /> : null}
        <Button title='Show overlay' testID={testIDs.SHOW_OVERLAY_BUTTON} onPress={() => this.onClickShowOverlay(true)} />
        <Button title='Show touch through overlay' testID={testIDs.SHOW_TOUCH_THROUGH_OVERLAY_BUTTON} onPress={() => this.onClickShowOverlay(false)} />
        <Button title='Push Default Options Screen' testID={testIDs.PUSH_DEFAULT_OPTIONS_BUTTON} onPress={this.onClickPushDefaultOptionsScreen} />
        <Button title='Show TopBar react view' testID={testIDs.SHOW_TOPBAR_REACT_VIEW} onPress={this.onShowTopBarReactView} />
        {Platform.OS === 'android' ? <Button title='Push' testID={testIDs.PUSH_BUTTON} onPress={this.onPush} /> : null}
        <Text style={styles.footer}>{`this.props.containerId = ${this.props.containerId}`}</Text>
      </View>
    );
  }

  onNavigationButtonPressed(id) {
    if (id === BUTTON_ONE) {
      Navigation.mergeOptions(this.props.componentId, {
        topBar: {
          rightButtons: [{
            id: BUTTON_TWO,
            testID: BUTTON_TWO,
            title: 'Two',
            icon: require('../../img/navicon_add.png'),
            disableIconTint: true,
            showAsAction: 'ifRoom',
            color: 'green',
            fontSize: 28,
            fontWeight: '800'
          }],
          leftButtons: []
        }
      });
    } else if (id === BUTTON_TWO) {
      Navigation.mergeOptions(this.props.componentId, {
        topBar: {
          rightButtons: [{
            id: BUTTON_ONE,
            testID: BUTTON_ONE,
            title: 'One',
            color: 'red'
          }],
          leftButtons: [{
            id: BUTTON_LEFT,
            testID: BUTTON_LEFT,
            icon: require('../../img/navicon_add.png'),
            title: 'Left',
            color: 'purple'
          }]
        }
      });
    } else if (id === BUTTON_LEFT) {
      Navigation.pop(this.props.componentId);
    }
  }

  onClickDynamicOptions = () => {
    Navigation.mergeOptions(this.props.componentId, {
      topBar: {
        title: {
          text: 'Dynamic Title',
          color: '#00FFFF',
          largeTitle: false,
          fontSize: 20,
          fontFamily: 'HelveticaNeue-CondensedBold'
        }
      }
    });
  }

  onClickScrollViewScreen = () => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'navigation.playground.ScrollViewScreen'
      }
    });
  }

  onClickCustomTranstition = () => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'navigation.playground.CustomTransitionOrigin'
      }
    });
  }

  onClickTopBarTransparent = () => {
    Navigation.mergeOptions(this.props.componentId, {
      topBar: {
        transparent: true
      }
    });
  }

  onClickTopBarOpaque = () => {
    Navigation.mergeOptions(this.props.componentId, {
      topBar: {
        transparent: false
      }
    });
  }

  onClickShowTopBar = () => {
    Navigation.mergeOptions(this.props.componentId, {
      topBar: {
        visible: true,
        animate: true
      }
    });
  }

  onClickHideTopBar = () => {
    Navigation.mergeOptions(this.props.componentId, {
      topBar: {
        visible: false,
        animate: true
      }
    });
  }

  onClickFab = () => {
    Navigation.mergeOptions(this.props.componentId, {
      fab: {
        id: FAB,
        visible: false
        // backgroundColor: 'green'
      }
    });
  }

  onClickShowOverlay = async (interceptTouchOutside) => {
    await Navigation.showOverlay({
      component: {
        name: 'navigation.playground.CustomDialog',
        options: {
          overlay: {
            interceptTouchOutside
          }
        }
      }
    });
  }

  onClickPushDefaultOptionsScreen = () => {
    Navigation.setDefaultOptions({
      topBar: {
        visible: false,
        animate: false
      }
    });

    Navigation.push(this.props.componentId, {
      component: {
        name: 'navigation.playground.PushedScreen'
      }
    });
  }

  onShowTopBarReactView = () => {
    Navigation.mergeOptions(this.props.componentId, {
      topBar: {
        title: {
          component: {
            name: 'navigation.playground.CustomTopBar',
            alignment: 'center'
          }
        }
      }
    });
  }

  onPush = () => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'navigation.playground.PushedScreen',
        options: {
          topBar: {
            title: {
              text: 'pushed'
            },
            subtitle: {
              text: 'subtitle'
            }
          }
        }
      }
    });
  }
}

const styles = {
  root: {
    flexGrow: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'white'
  },
  h1: {
    fontSize: 24,
    textAlign: 'center',
    margin: 10
  },
  h2: {
    fontSize: 12,
    textAlign: 'center',
    margin: 10
  },
  footer: {
    fontSize: 10,
    color: '#888',
    marginTop: 10
  }
};

module.exports = OptionsScreen;
