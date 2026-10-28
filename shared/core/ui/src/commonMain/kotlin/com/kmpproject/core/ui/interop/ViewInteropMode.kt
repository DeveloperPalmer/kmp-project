package com.kmpproject.core.ui.interop

/**
 * Which implementation draws the city details screen on Android
 */
enum class ViewInteropMode {
  /** The shared Compose UI, as on iOS */
  Compose,

  /** A fragment that hosts the shared Compose UI in a ComposeView */
  FragmentCompose,

  /** A fragment with its own XML layout on Android Views */
  FragmentView,

  /** The same fragment on Android Views, whose views come from a generated ViewBinding */
  FragmentViewBinding,

  /** The fragment on ViewBinding whose binding inflates the layout itself in onCreateView */
  FragmentViewBindingInflate,
}
