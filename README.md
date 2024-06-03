TCDCommons is a Minecraft modding API library that features its own GUI system and various events and hooks for the game, as well as utilities that may be useful to mod developers. This API's main purpose is to help optimize and speed up the development of mods, as well as to avoiding re-writing the same code for every mod that needs to do similar things. Please note that because this mod is a library, it may not offer useful front-end features to the user.

## Purpose
As mentioned earlier, the primary goal of this API is to enhance and accelerate the development of Minecraft mods, while also eliminating the need to rewrite redundant code for each mod that performs similar tasks. In the beginning, the initial purpose of this API mod was to provide a better the GUI system that's easier to work with, but it has since evolved to include various useful events, hooks, and utilities that may be commonly used.

### Mods that use this API
This API mod is primarily used by [TheCSDev](https://github.com/TheCSDev) for developing [Better Statistics Screen](https://github.com/TheCSMods/mc-better-stats).

## API features
### Client-side
- **GUI System**: A unique, user-friendly UI system that aims to be an improvement from the vanilla GUI system in terms of customization and flexibility.
- **HUD Screens**: This feature enables the rendering of `Screen`s on the in-game HUD for visual enhancement. Please note, these HUD `Screen`s are for display purposes only and cannot be interacted with via user input.

### Common-side
- **Auto-config system**: Simplify the creation of JSON configuration files for your mod with this system. It automatically serializes fields of primitive types defined in the config `Class` during the saving and loading of configs.
- **Event System**: Allows you to listen for various in-game events. It also allows you to define custom events for your mod, which other mods can listen to.
- **Hooks**: Gain easy access to various game components with the help of these hooks.
- **Networking**: This API comes with its own custom-payload-network, enabling other mods to communicate in both C2S and S2C directions over the game's network protocol.
- **Utilities**: Benefit from a range of utilities, including various interfaces, enums, exception types, IO utilities, threading, and more. One such utility is a caching system that allows mods to perform asynchronous operations to fetch/load resources and cache them.

## A simple introduction to the GUI system
With the GUI system, you can create your own custom GUI menus, in a simpler way that allows for more customization and flexibility.  
In the vanilla GUI system, we use `Screen`s. In this API's GUI system, we use what's called `TScreen`s.

All GUI components, including `TScreen`, and GUI widgets, can be found in the following package:
`io.github.thecsdev.tcdcommons.api.client.gui`.

Below we will create an example `TScreen` implementation, with a simple button on it that does something when it's clicked:
```java
import io.github.thecsdev.tcdcommons.api.client.gui.screen.TScreen;
import io.github.thecsdev.tcdcommons.api.client.gui.widget.TButtonWidget;
import net.minecraft.text.Text;

public final class ExampleTScreen extends TScreen
{
	/*
	 * `super` takes a `Text` argument for the screen's title
	 */
	public ExampleTScreen() { super(Text.literal("Example TScreen")); }
	
	/*
	 * This is where the screen initializes its GUI elements.
	 * We will add the button widget here.
	 */
	protected final @Override void init()
	{
		//create the button, and center it on the screen
		final var button = new TButtonWidget(
			(getWidth() / 2) - 50,  //the button's X position. we will center it
			(getHeight() / 2) - 10, //the button's Y position. we will center it
			100, //the button's width
			20   //the button's height
		);
		
		//define a text for the button
		button.setText(Text.literal("Click me"));
		
		//now let's do something when the button is clicked
		button.setOnClick(btn ->
		{
			//print a simple message to the console
			//when the button is clicked
			System.out.println("Hello World!");
		});
		
		//and finally, let's add the button to the screen
		addChild(button);
	}
}
```

Now, we need to open the screen and show it to the user. To do so, we use `MinecraftClient#setScreen(Screen)` (on Fabric), and we use `TScreen#getAsScreen()` to obtain the `Screen` reference. So our final code will look like this:
```java
final var tscreen = new ExampleTScreen();
final var screen = tscreen.getAsScreen();
MinecraftClient.getInstance().setScreen(screen);
```

## A simple introduction to the event system
The event system allows you to not only listen for various events already provided by this API, but it also allows you to create and invoke your own custom events.  
Below is an example of how you can create your own event, register and unregister listeners, as well as invoke the event:
```java
import io.github.thecsdev.tcdcommons.api.event.TEvent;
import io.github.thecsdev.tcdcommons.api.event.TEventFactory;

public final class ExampleEvents
{
	/*
	 * This is how we create a custom event using this API.
	 * Note that this event system is similar to Architectury API's event system,
	 * but with some tweaks and additions done to it, for more flexibility.
	 */
	public static final TEvent<Runnable> eSomething = TEventFactory.createLoop();
	
	public static final void registerAndUnregisterListener()
	{
		//we first define the listener that will be invoked when the event is invoked
		final Runnable listener = () -> { System.out.println("The event was invoked."); };
		
		//then, this is how we can register it
		eSomething.register(listener);
		
		//and then, this is how we can unregister it
		eSomething.unregister(listener);
	}
	
	public static final void invokeEvent()
	{
		//we invoke the event by obtaining the "invoker",
		//and calling the functional interface's main function
		//(in Runnable's case, the main function is `run`)
		eSomething.invoker().run();
	}
}
```

## Contributing
I appreciate and value everyone's contributions to this mod, whether it involves translating the mod, offering feedback, or providing valuable suggestions. If you’d like to contribute, here are some ways you can get involved:
1. If you’re proficient in multiple languages, consider helping me translate the mod into other languages. Your efforts will make the mod accessible to a broader audience.
2. Your ideas and suggestions are invaluable! Feel free to share your thoughts on how the mod can be improved. Whether it’s a bug report, a feature request, or an enhancement idea. I would love to get some feedback from the community.
3. If you encounter any issues or have ideas for enhancements, open an issue on this API mod's repository page.

## Other
For more information about this mod, please visit [this mod's repository page](https://github.com/TheCSMods/mc-tcdcommons).  
For information about this mod's license, please view [this mod repository's license page](https://github.com/TheCSMods/mc-tcdcommons/blob/main/LICENSE).