# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

Open in Android Studio (Hedgehog or later). Gradle sync runs automatically. There are no custom Gradle tasks.

Build from CLI:
```bash
./gradlew assembleDebug          # build APK
./gradlew test                   # run unit tests
./gradlew testDebugUnitTest      # unit tests (debug variant only)
./gradlew connectedAndroidTest   # instrumented tests (requires device/emulator)
```

Run a single test class:
```bash
./gradlew testDebugUnitTest --tests "com.example.cinefind.domain.usecase.GetMovieUseCaseTest"
```

## Architecture

Single-Activity (`MovieActivity`) + Fragment pattern with manual `FragmentManager` transactions. No Jetpack Navigation / NavController.

**Layers:**

- **Data** — `data/service/MovieApiService` (Retrofit), `data/repository/MovieRepository` (interface) + `MovieRepositoryImpl` (impl). Repository methods return sealed `*State` classes.
- **Domain** — use cases extend `BaseUseCase<Result, Params>` or `BaseUseCaseNoParams<Result>`. Each has an `invoke(params)` abstract method and an `execute(params)` convenience wrapper. All live in `domain/usecase/`.
- **Presentation** — `MovieViewModel` (`@HiltViewModel`) holds all state. `MovieActivity` hosts fragments; `MovieListFragment`, `FavoritesFragment`, and `MovieDetailFragment` all use `activityViewModels<MovieViewModel>()` to share the same instance.

**State / events:** domain results come back as sealed `MovieState` / `GenreState` / `MovieDetailState`. One-shot UI events use `SingleLiveEvent<T>`. `BaseViewModel.launchSafely(execute, onSuccess, onError?)` is the coroutine helper — it auto-shows/hides progress and catches exceptions.

**DI:** Two Hilt `@SingletonComponent` modules — `NetworkModule` (OkHttp, Retrofit, `MovieApiService`) and `MovieModule` (repository binding + all use cases as `@Singleton`). Use cases are `@Inject constructor` but also explicitly provided in `MovieModule` — follow that pattern for new use cases.

**Navigation:** `MovieActivity.switchTab()` replaces the `fragmentContainer` by tag (reusing existing instances). `MovieDetailFragment` is pushed via `addToBackStack(null)`. The `BottomNavigationView` hides when `backStackEntryCount > 0`.

## Key Patterns

- **New use case:** extend `BaseUseCase<ReturnType, ParamType>`, `@Inject constructor(private val repository: MovieRepository)`, override `invoke`. Add a `@Singleton @Provides` entry in `MovieModule`.
- **New repo method:** add to `MovieRepository` interface, implement in `MovieRepositoryImpl` using `withContext(Dispatchers.IO)` for the API call, wrap result in the appropriate `*State` sealed class.
- **Tests:** JUnit 4 + MockK (`coEvery`) + `kotlinx-coroutines-test` (`runTest`, `UnconfinedTestDispatcher`) + `InstantTaskExecutorRule` for LiveData. See `GetUpcomingMovieUseCaseTest` or `MovieViewModelTest` for the established pattern.
- **View Binding** is used everywhere — no `findViewById`. Fragments nullify `_binding` in `onDestroyView`.

## Tech Stack

Kotlin · Hilt 2.60 · Retrofit 2.11 + OkHttp 4.12 + Gson · Coil 2.7 · Coroutines 1.9 · LiveData + ViewModel (lifecycle 2.8.7) · Material 1.10 · ConstraintLayout 2.2 · minSdk 24 / targetSdk 36
