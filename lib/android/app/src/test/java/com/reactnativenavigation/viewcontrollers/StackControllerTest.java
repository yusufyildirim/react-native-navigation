package com.reactnativenavigation.viewcontrollers;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.anim.NavigationAnimator;
import com.reactnativenavigation.mocks.SimpleViewController;
import com.reactnativenavigation.mocks.TitleBarReactViewCreatorMock;
import com.reactnativenavigation.mocks.TopBarBackgroundViewCreatorMock;
import com.reactnativenavigation.mocks.TopBarButtonCreatorMock;
import com.reactnativenavigation.parse.NestedAnimationsOptions;
import com.reactnativenavigation.parse.Options;
import com.reactnativenavigation.parse.params.Bool;
import com.reactnativenavigation.parse.params.Button;
import com.reactnativenavigation.parse.params.Text;
import com.reactnativenavigation.utils.CommandListenerAdapter;
import com.reactnativenavigation.utils.ViewHelper;
import com.reactnativenavigation.viewcontrollers.topbar.TopBarBackgroundViewController;
import com.reactnativenavigation.viewcontrollers.topbar.TopBarController;
import com.reactnativenavigation.views.Component;
import com.reactnativenavigation.views.ReactComponent;
import com.reactnativenavigation.views.StackLayout;
import com.reactnativenavigation.views.titlebar.TitleBarReactViewCreator;
import com.reactnativenavigation.views.topbar.TopBar;

import org.assertj.core.api.iterable.Extractor;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class StackControllerTest extends BaseTest {

    private Activity activity;
    private ChildControllersRegistry childRegistry;
    private StackController uut;
    private ViewController child1;
    private ViewController child2;
    private ViewController child3;
    private ViewController child4;
    private NavigationAnimator animator;
    private TopBarController topBarController;

    @Override
    public void beforeEach() {
        super.beforeEach();
        animator = Mockito.mock(NavigationAnimator.class);
        activity = newActivity();
        childRegistry = new ChildControllersRegistry();
        uut = createStackController();
        child1 = spy(new SimpleViewController(activity, childRegistry, "child1", new Options()));
        child2 = spy(new SimpleViewController(activity, childRegistry, "child2", new Options()));
        child3 = spy(new SimpleViewController(activity, childRegistry, "child3", new Options()));
        child4 = spy(new SimpleViewController(activity, childRegistry, "child4", new Options()));
    }

    @Test
    public void isAViewController() {
        assertThat(uut).isInstanceOf(ViewController.class);
    }

    @Test
    public void holdsAStackOfViewControllers() {
        assertThat(uut.isEmpty()).isTrue();
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        uut.push(child3, new CommandListenerAdapter());
        assertThat(uut.peek()).isEqualTo(child3);
        assertContainsOnlyId(child1.getId(), child2.getId(), child3.getId());
    }

    @Test
    public void push() {
        assertThat(uut.isEmpty()).isTrue();
        CommandListenerAdapter listener = spy(new CommandListenerAdapter());
        uut.push(child1, listener);
        assertContainsOnlyId(child1.getId());
        verify(listener, times(1)).onSuccess(child1.getId());
    }

    @Test
    public void push_backButtonIsAddedIfStackContainsMoreThenOneScreen() {
        uut.push(child1, new CommandListenerAdapter());
        verify(child1, times(0)).mergeOptions(any());

        uut.push(child2, new CommandListenerAdapter());
        ArgumentCaptor<Options> captor = ArgumentCaptor.forClass(Options.class);
        verify(child2, times(1)).mergeOptions(captor.capture());
        assertThat(captor.getValue().topBar.leftButtons).isNotNull();
        assertThat(captor.getValue().topBar.leftButtons.get(0).id).isEqualTo("RNN.back");
    }

    @Test
    public void push_backButtonIsNotAddedIfScreenContainsLeftButtons() {
        uut.push(child1, new CommandListenerAdapter());

        Button leftButton = new Button();
        leftButton.id = "someButton";
        child2.options.topBar.leftButtons = new ArrayList<>(Collections.singleton(leftButton));
        uut.push(child2, new CommandListenerAdapter());
        verify(child2, times(0)).mergeOptions(any());
    }

    @Test
    public void push_backButtonIsNotAddedIfScreenClearsLeftButton() {
        child1.options.topBar.leftButtons = new ArrayList<>();
        uut.push(child1, new CommandListenerAdapter());
        verify(child1, times(0)).mergeOptions(any());
    }

    @Test
    public void animateSetRoot() {
        assertThat(uut.isEmpty()).isTrue();
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        uut.setRoot(child3, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                assertContainsOnlyId(child3.getId());
            }
        });
    }

    @Test
    public void setRoot() {
        assertThat(uut.isEmpty()).isTrue();
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        assertThat(uut.getTopBar().getTitleBar().getNavigationIcon()).isNotNull();
        uut.setRoot(child3, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                assertContainsOnlyId(child3.getId());
                assertThat(uut.getTopBar().getTitleBar().getNavigationIcon()).isNull();
            }
        });
    }

    @Test
    public void pop() {
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                assertContainsOnlyId(child2.getId(), child1.getId());
                uut.pop(new CommandListenerAdapter());
                assertContainsOnlyId(child1.getId());
            }
        });
    }

    @Test
    public void pop_appliesOptionsAfterPop() {
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                uut.pop(new CommandListenerAdapter());
                verify(uut, times(1)).applyChildOptions(uut.options, eq((ReactComponent) child1.getView()));
            }
        });
    }

    @Test
    public void pop_layoutHandlesChildWillDisappear() {
        final StackLayout[] stackLayout = new StackLayout[1];
        uut = new StackControllerBuilder(activity)
                        .setTopBarButtonCreator(new TopBarButtonCreatorMock())
                        .setTitleBarReactViewCreator(new TitleBarReactViewCreatorMock())
                        .setTopBarBackgroundViewController(new TopBarBackgroundViewController(activity, new TopBarBackgroundViewCreatorMock()))
                        .setTopBarController(new TopBarController())
                        .setId("uut")
                        .setInitialOptions(new Options())
                        .createStackController();
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                uut.pop(new CommandListenerAdapter() {
                    @Override
                    public void onSuccess(String childId) {
                        verify(stackLayout[0], times(1)).onChildWillAppear(child1, child2);
                    }
                });
            }
        });
    }

    @Test
    public void stackOperations() {
        assertThat(uut.peek()).isNull();
        assertThat(uut.size()).isZero();
        assertThat(uut.isEmpty()).isTrue();
        uut.push(child1, new CommandListenerAdapter());
        assertThat(uut.peek()).isEqualTo(child1);
        assertThat(uut.size()).isEqualTo(1);
        assertThat(uut.isEmpty()).isFalse();
    }

    @Test
    public void handleBack_PopsUnlessSingleChild() {
        assertThat(uut.isEmpty()).isTrue();
        assertThat(uut.handleBack(new CommandListenerAdapter())).isFalse();

        uut.push(child1, new CommandListenerAdapter());
        assertThat(uut.size()).isEqualTo(1);
        assertThat(uut.handleBack(new CommandListenerAdapter())).isFalse();

        uut.push(child2, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                assertThat(uut.size()).isEqualTo(2);
                assertThat(uut.handleBack(new CommandListenerAdapter())).isTrue();

                assertThat(uut.size()).isEqualTo(1);
                assertThat(uut.handleBack(new CommandListenerAdapter())).isFalse();
            }
        });
    }

    @Test
    public void popDoesNothingWhenZeroOrOneChild() {
        assertThat(uut.isEmpty()).isTrue();
        uut.pop(new CommandListenerAdapter());
        assertThat(uut.isEmpty()).isTrue();

        uut.push(child1, new CommandListenerAdapter());
        uut.pop(new CommandListenerAdapter());
        assertContainsOnlyId(child1.getId());
    }

    @Test
    public void canPopWhenSizeIsMoreThanOne() {
        assertThat(uut.isEmpty()).isTrue();
        assertThat(uut.canPop()).isFalse();
        uut.push(child1, new CommandListenerAdapter());
        assertContainsOnlyId(child1.getId());
        assertThat(uut.canPop()).isFalse();
        uut.push(child2, new CommandListenerAdapter());
        assertContainsOnlyId(child1.getId(), child2.getId());
        assertThat(uut.canPop()).isTrue();
    }

    @Test
    public void push_addsToViewTree() {
        assertNotChildOf(uut.getView(), child1.getView());
        uut.push(child1, new CommandListenerAdapter());
        assertIsChild(uut.getView(), child1.getView());
    }

    @Test
    public void push_removesPreviousFromTree() {
        assertNotChildOf(uut.getView(), child1.getView());
        uut.push(child1, new CommandListenerAdapter());
        assertIsChild(uut.getView(), child1.getView());
        uut.push(child2, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                assertThat(uut.getView().findViewById(child1.getView().getId())).isNull();
                assertThat(uut.getView().findViewById(child2.getView().getId())).isNotNull();
            }
        });
    }

    @Test
    public void push_assignsRefToSelfOnPushedController() {
        assertThat(child1.getParentController()).isNull();
        uut.push(child1, new CommandListenerAdapter());
        assertThat(child1.getParentController()).isEqualTo(uut);

        StackController anotherNavController = createStackController("another");
        anotherNavController.push(child2, new CommandListenerAdapter());
        assertThat(child2.getParentController()).isEqualTo(anotherNavController);
    }

    @Test
    public void push_doesNotAnimateTopBarIfScreenIsPushedWithoutAnimation() {
        uut.ensureViewIsCreated();
        child1.ensureViewIsCreated();

        child1.options.topBar.visible = new Bool(false);
        child1.options.topBar.animate = new Bool(false);
        disablePushAnimation(child1, child2);

        uut.push(child1, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                child1.onViewAppeared();
                assertThat(uut.getTopBar().getVisibility()).isEqualTo(View.GONE);

                uut.push(child2, new CommandListenerAdapter());
                child2.onViewAppeared();
                verify(uut.getTopBar(), times(0)).showAnimate(child2.options.animations.push.topBar);
                assertThat(uut.getTopBar().getVisibility()).isEqualTo(View.VISIBLE);
                verify(uut.getTopBar(), times(1)).resetAnimationOptions();
            }
        });
    }

    @Test
    public void push_animatesAndClearsPreviousAnimationValues() {
        uut.ensureViewIsCreated();

        child1.options.topBar.visible = new Bool(false);
        child1.options.topBar.animate = new Bool(false);
        child1.options.animations.push.enable = new Bool(false);

        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                verify(uut.getTopBar(), times(1)).resetAnimationOptions();
            }
        });
    }

    @Test
    public void pop_replacesViewWithPrevious() {
        final View child2View = child2.getView();
        final View child1View = child1.getView();

        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                assertIsChild(uut.getView(), child2View);
                assertNotChildOf(uut.getView(), child1View);
                uut.pop(new CommandListenerAdapter());
                assertNotChildOf(uut.getView(), child2View);
                assertIsChild(uut.getView(), child1View);
            }
        });
    }

    @Test
    public void pop_specificWhenTopIsRegularPop() {
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                uut.popSpecific(child2, new CommandListenerAdapter() {
                    @Override
                    public void onSuccess(String childId) {
                        assertContainsOnlyId(child1.getId());
                        assertIsChild(uut.getView(), child1.getView());
                    }
                });
            }
        });
    }

    @Test
    public void popSpecific_deepInStack() {
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        assertIsChild(uut.getView(), child2.getView());
        uut.popSpecific(child1, new CommandListenerAdapter());
        assertContainsOnlyId(child2.getId());
        assertIsChild(uut.getView(), child2.getView());
    }

    @Test
    public void popTo_PopsTopUntilControllerIsNewTop() {
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        uut.push(child3, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                assertThat(uut.size()).isEqualTo(3);
                assertThat(uut.peek()).isEqualTo(child3);

                uut.popTo(child1, new CommandListenerAdapter());

                assertThat(uut.size()).isEqualTo(1);
                assertThat(uut.peek()).isEqualTo(child1);
            }
        });
    }

    @Test
    public void popTo_NotAChildOfThisStack_DoesNothing() {
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child3, new CommandListenerAdapter());
        assertThat(uut.size()).isEqualTo(2);
        uut.popTo(child2, new CommandListenerAdapter());
        assertThat(uut.size()).isEqualTo(2);
    }

    @Test
    public void popTo_animatesTopController() {
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        uut.push(child3, new CommandListenerAdapter());
        uut.push(child4, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                uut.popTo(child2, new CommandListenerAdapter() {
                    @Override
                    public void onSuccess(String childId) {
                        verify(animator, times(0)).pop(eq(child1.getView()), any());
                        verify(animator, times(0)).pop(eq(child2.getView()), any());
                        verify(animator, times(1)).pop(eq(child4.getView()), any());
                    }
                });
            }
        });
    }

    @Test
    public void popToRoot_PopsEverythingAboveFirstController() {
        child1.options.animations.push.enable = new Bool(false);
        child2.options.animations.push.enable = new Bool(false);

        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        uut.push(child3, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                assertThat(uut.size()).isEqualTo(3);
                assertThat(uut.peek()).isEqualTo(child3);

                uut.popToRoot(new CommandListenerAdapter() {
                    @Override
                    public void onSuccess(String childId) {
                        assertThat(uut.size()).isEqualTo(1);
                        assertThat(uut.peek()).isEqualTo(child1);
                    }
                });
            }
        });
    }

    @Test
    public void popToRoot_onlyTopChildIsAnimated() {
        child1.options.animations.push.enable = new Bool(false);
        child2.options.animations.push.enable = new Bool(false);

        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        uut.push(child3, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                uut.popToRoot(new CommandListenerAdapter() {
                    @Override
                    public void onSuccess(String childId) {
                        verify(animator, times(1)).pop(eq(child3.getView()), any());
                    }
                });
            }
        });
    }

    @Test
    public void popToRoot_topChildrenAreDestroyed() {
        child1.options.animations.push.enable = new Bool(false);
        child2.options.animations.push.enable = new Bool(false);
        child3.options.animations.push.enable = new Bool(false);

        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        uut.push(child3, new CommandListenerAdapter());

        uut.popToRoot(new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                verify(child1, times(0)).destroy();
                verify(child2, times(1)).destroy();
                verify(child3, times(1)).destroy();
            }
        });
    }

    @Test
    public void popToRoot_EmptyStackDoesNothing() {
        assertThat(uut.isEmpty()).isTrue();
        CommandListenerAdapter listener = spy(new CommandListenerAdapter());
        uut.popToRoot(listener);
        assertThat(uut.isEmpty()).isTrue();
        verify(listener, times(1)).onError(any());
    }

    @Test
    public void findControllerById_ReturnsSelfOrChildrenById() {
        assertThat(uut.findControllerById("123")).isNull();
        assertThat(uut.findControllerById(uut.getId())).isEqualTo(uut);
        uut.push(child1, new CommandListenerAdapter());
        assertThat(uut.findControllerById(child1.getId())).isEqualTo(child1);
    }

    @Test
    public void findControllerById_Deeply() {
        StackController stack = createStackController("another");
        stack.push(child2, new CommandListenerAdapter());
        uut.push(stack, new CommandListenerAdapter());
        assertThat(uut.findControllerById(child2.getId())).isEqualTo(child2);
    }

    @Test
    public void pop_CallsDestroyOnPoppedChild() {
        child1 = spy(child1);
        child2 = spy(child2);
        child3 = spy(child3);
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        uut.push(child3, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                verify(child3, times(0)).destroy();
                uut.pop(new CommandListenerAdapter());
                verify(child3, times(1)).destroy();
            }
        });
    }

    @Test
    public void pop_callWillAppearWillDisappear() {
        child1.options.animations.push.enable = new Bool(false);
        child2.options.animations.push.enable = new Bool(false);
        child1 = spy(child1);
        child2 = spy(child2);
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        uut.pop(new CommandListenerAdapter());
        verify(child1, times(1)).onViewWillAppear();
        verify(child2, times(1)).onViewWillDisappear();
    }

    @Test
    public void pop_animatesTopBar() {
        uut.ensureViewIsCreated();

        child1.options.topBar.visible = new Bool(false);
        child1.options.animations.push.enable = new Bool(false);
        child2.options.animations.push.enable = new Bool(true);
        uut.push(child1, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                child1.onViewAppeared();
                assertThat(uut.getTopBar().getVisibility()).isEqualTo(View.GONE);
                uut.push(child2, new CommandListenerAdapter() {
                    @Override
                    public void onSuccess(String childId) {
                        uut.pop(new CommandListenerAdapter() {
                            @Override
                            public void onSuccess(String childId) {
                                verify(uut.getTopBar(), times(1)).hideAnimate(child2.options.animations.pop.topBar);
                            }
                        });
                    }
                });
            }
        });
    }

    @Test
    public void pop_doesNotAnimateTopBarIfScreenIsPushedWithoutAnimation() {
        uut.ensureViewIsCreated();

        child1.options.topBar.visible = new Bool(false);
        child1.options.topBar.animate = new Bool(false);
        child2.options.animations.push.enable = new Bool(false);
        child2.options.topBar.animate = new Bool(false);

        child1.ensureViewIsCreated();
        uut.push(child1, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                uut.push(child2, new CommandListenerAdapter());
                assertThat(uut.getTopBar().getVisibility()).isEqualTo(View.VISIBLE);

                uut.pop(new CommandListenerAdapter());
                verify(uut.getTopBar(), times(0)).hideAnimate(child2.options.animations.pop.topBar);
                assertThat(uut.getTopBar().getVisibility()).isEqualTo(View.GONE);
            }
        });
    }

    @Test
    public void popSpecific_CallsDestroyOnPoppedChild() {
        child1 = spy(child1);
        child2 = spy(child2);
        child3 = spy(child3);
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        uut.push(child3, new CommandListenerAdapter());

        verify(child2, times(0)).destroy();
        uut.popSpecific(child2, new CommandListenerAdapter());
        verify(child2, times(1)).destroy();
    }

    @Test
    public void popTo_CallsDestroyOnPoppedChild() {
        child1 = spy(child1);
        child2 = spy(child2);
        child3 = spy(child3);
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        uut.push(child3, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                verify(child2, times(0)).destroy();
                verify(child3, times(0)).destroy();

                uut.popTo(child1, new CommandListenerAdapter() {
                    @Override
                    public void onSuccess(String childId) {
                        verify(child2, times(1)).destroy();
                        verify(child3, times(1)).destroy();
                    }
                });
            }
        });
    }

    @Test
    public void stackCanBePushed() {
        StackController parent = createStackController("someStack");
        parent.ensureViewIsCreated();
        parent.push(uut, new CommandListenerAdapter());
        uut.onViewAppeared();
        assertThat(parent.getView().getChildAt(1)).isEqualTo(uut.getView());
    }

    @Test
    public void applyOptions_applyOnlyOnFirstStack() {
        StackController parent = spy(createStackController("someStack"));
        parent.ensureViewIsCreated();
        parent.push(uut, new CommandListenerAdapter());

        Options childOptions = new Options();
        childOptions.topBar.title.text = new Text("Something");
        child1.options = childOptions;
        uut.push(child1, new CommandListenerAdapter());
        child1.ensureViewIsCreated();
        child1.onViewAppeared();

        ArgumentCaptor<Options> optionsCaptor = ArgumentCaptor.forClass(Options.class);
        ArgumentCaptor<ReactComponent> viewCaptor = ArgumentCaptor.forClass(ReactComponent.class);
        verify(parent, times(1)).applyChildOptions(optionsCaptor.capture(), viewCaptor.capture());
        assertThat(optionsCaptor.getValue().topBar.title.text.hasValue()).isFalse();
    }

    @Test
    public void applyOptions_topTabsAreNotVisibleIfNoTabsAreDefined() {
        uut.ensureViewIsCreated();
        uut.push(child1, new CommandListenerAdapter());
        child1.ensureViewIsCreated();
        child1.onViewAppeared();
        assertThat(ViewHelper.isVisible(uut.getTopBar().getTopTabs())).isFalse();
    }

    @Test
    public void buttonPressInvokedOnCurrentStack() {
        uut.ensureViewIsCreated();
        uut.push(child1, new CommandListenerAdapter());
        uut.sendOnNavigationButtonPressed("btn1");
        verify(child1, times(1)).sendOnNavigationButtonPressed("btn1");
    }

    @Test
    public void mergeChildOptions_updatesViewWithNewOptions() {
        StackController uut = spy(new StackControllerBuilder(activity)
                        .setTopBarButtonCreator(new TopBarButtonCreatorMock())
                        .setTitleBarReactViewCreator(new TitleBarReactViewCreatorMock())
                        .setTopBarBackgroundViewController(new TopBarBackgroundViewController(activity, new TopBarBackgroundViewCreatorMock()))
                        .setTopBarController(new TopBarController())
                        .setId("stack")
                        .setInitialOptions(new Options())
                        .createStackController());
        Options optionsToMerge = new Options();
        Component component = mock(Component.class);
        uut.mergeChildOptions(optionsToMerge, component);
        verify(uut, times(1)).mergeChildOptions(optionsToMerge, component);
    }

    @Test
    public void mergeChildOptions_updatesParentControllerWithNewOptions() {
        StackController uut = new StackControllerBuilder(activity)
                        .setTopBarButtonCreator(new TopBarButtonCreatorMock())
                        .setTitleBarReactViewCreator(new TitleBarReactViewCreatorMock())
                        .setTopBarBackgroundViewController(new TopBarBackgroundViewController(activity, new TopBarBackgroundViewCreatorMock()))
                        .setTopBarController(new TopBarController())
                        .setId("stack")
                        .setInitialOptions(new Options())
                        .createStackController();
        ParentController parentController = Mockito.mock(ParentController.class);
        uut.setParentController(parentController);
        Options optionsToMerge = new Options();
        optionsToMerge.topBar.testId = new Text("topBarID");
        optionsToMerge.bottomTabsOptions.testId = new Text("bottomTabsID");
        Component component = mock(Component.class);
        uut.mergeChildOptions(optionsToMerge, component);

        ArgumentCaptor<Options> captor = ArgumentCaptor.forClass(Options.class);
        verify(parentController, times(1)).mergeChildOptions(captor.capture(), eq(component));
        assertThat(captor.getValue().topBar.testId.hasValue()).isFalse();
        assertThat(captor.getValue().bottomTabsOptions.testId.get()).isEqualTo(optionsToMerge.bottomTabsOptions.testId.get());
    }

    @Test
    public void mergeChildOptions_mergeAnimationOptions() {
        Options options = new Options();
        Component component = mock(Component.class);

        uut.mergeChildOptions(options, component);
        verify(animator, times(0)).setOptions(options.animations);
        verify(animator, times(1)).mergeOptions(options.animations);
    }

    @Test
    public void mergeChildOptions_StackRelatedOptionsAreCleared() {
        ParentController parentController = Mockito.mock(ParentController.class);
        uut.setParentController(parentController);
        Options options = new Options();
        options.animations.push = NestedAnimationsOptions.parse(new JSONObject());
        options.topBar.testId = new Text("id");
        options.fabOptions.id = new Text("fabId");
        Component component = mock(Component.class);

        assertThat(options.fabOptions.hasValue()).isTrue();
        uut.mergeChildOptions(options, component);
        ArgumentCaptor<Options> captor = ArgumentCaptor.forClass(Options.class);
        verify(parentController, times(1)).mergeChildOptions(captor.capture(), eq(component));
        assertThat(captor.getValue().animations.push.hasValue()).isFalse();
        assertThat(captor.getValue().topBar.testId.hasValue()).isFalse();
        assertThat(captor.getValue().fabOptions.hasValue()).isFalse();
    }

    @Test
    public void destroy() {
        uut.ensureViewIsCreated();
        uut.destroy();
        verify(topBarController, times(1)).clear();
    }

    private void assertContainsOnlyId(String... ids) {
        assertThat(uut.size()).isEqualTo(ids.length);
        assertThat(uut.getChildControllers()).extracting((Extractor<ViewController, String>) ViewController::getId).containsOnly(ids);
    }

    private StackController createStackController() {
        return createStackController("stackId");
    }

    private StackController createStackController(String id) {
        createTopBarController();
        return new StackControllerBuilder(activity)
                .setChildRegistry(childRegistry)
                .setTopBarButtonCreator(new TopBarButtonCreatorMock())
                .setTitleBarReactViewCreator(new TitleBarReactViewCreatorMock())
                .setTopBarBackgroundViewController(new TopBarBackgroundViewController(activity, new TopBarBackgroundViewCreatorMock()))
                .setTopBarController(topBarController)
                .setAnimator(animator)
                .setId(id)
                .setInitialOptions(new Options())
                .createStackController();
    }

    private void createTopBarController() {
        topBarController = spy(new TopBarController() {
            @Override
            protected TopBar createTopBar(Context context, ReactViewCreator buttonCreator, TitleBarReactViewCreator titleBarReactViewCreator, TopBarBackgroundViewController topBarBackgroundViewController, TopBarButtonController.OnClickListener topBarButtonClickListener, StackLayout stackLayout) {
                return spy(super.createTopBar(context, buttonCreator, titleBarReactViewCreator, topBarBackgroundViewController, topBarButtonClickListener, stackLayout));
            }
        });
    }
}
