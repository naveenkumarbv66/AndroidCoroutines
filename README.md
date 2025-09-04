AndroidCoroutines
=================

A Jetpack Compose Android app demonstrating Kotlin Coroutines with Lifecycle, ViewModel (MVVM), WorkManager, and cancellation patterns.

Features
 - Lifecycle-aware coroutines via `lifecycleScope.launch`
 - `withContext(Dispatchers.IO)` for background work
 - Parallel work with `async`/`await`
 - Cancellation using `Job` references (Activity + ViewModel)
 - WorkManager + `CoroutineWorker` examples
 - MVVM with `StateFlow` + Compose UI

Key Modules / Files
 - Activities
   - `MainActivity` — lifecycleScope + withContext/async/launch demos (Compose UI)
   - `AsynCoroutinesDemo` — async demo with cancel support
   - `WorkManagerDemoActivity` — start/cancel WorkManager jobs (withContext worker + async worker)
   - `ViewModelCoroutinesDemo` — MVVM: ViewModel exposes flows; buttons run withContext/async and a cancellable withContext demo
 - ViewModels
   - `viewmodel/CorotinesDemo.kt` — `StateFlow<String>` status, `runWithContextDemo()`, `runAsyncDemo()`, cancellable withContext demo
   - `viewmodel/WorkerViewModel.kt` — wraps WorkManager via repository; exposes `LiveData<WorkInfo?>`
 - Repository
   - `data/WorkRepository.kt` — enqueues/cancels workers and exposes `WorkInfo` LiveData
 - Workers
   - `work/WithContextWorker.kt` — parameterized IO work with `withContext`, supports input `repeatCount`, `delayMs`, `message`
   - `work/AsyncWorker.kt` — parallel work with `async/await` in a `CoroutineWorker`

Dependencies (high level)
 - Kotlin Coroutines (core, android)
 - Jetpack Compose (Material3, runtime-livedata)
 - Lifecycle (runtime-ktx, viewmodel-ktx, viewmodel-compose)
 - WorkManager (work-runtime-ktx)

Running
 1. Open the project in Android Studio.
 2. Sync Gradle.
 3. Select a launcher activity in `AndroidManifest.xml` (e.g., `ViewModelCoroutinesDemo` or `WorkManagerDemoActivity`).
 4. Run on device/emulator (minSdk 24).

Demos
 - ViewModelCoroutinesDemo
   - Run withContext(): background chunks with progress via `StateFlow`
   - Run async(): two parallel tasks combined
   - Run withContext() Cancellable: toggle start/cancel; progress + cancellation
 - WorkManagerDemoActivity
   - Start/Cancel withContext Worker (with and without input params)
   - Start/Cancel Async Worker

Notes
 - Coroutines are lifecycle-aware (Activity) or use `viewModelScope` (ViewModel).
 - Cancellation shown via `Job.cancel()` and WorkManager `cancelWorkById`.
 - Compose observes state via `collectAsState()` and `observeAsState()` for LiveData.


