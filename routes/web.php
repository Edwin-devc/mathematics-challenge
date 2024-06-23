<?php

use App\Http\Controllers\ProfileController;
use App\Http\Controllers\RepresentativeController;
use App\Http\Controllers\SchoolController;
use App\Http\Controllers\ChallengeController;
use App\Models\Challenge;
use Illuminate\Support\Facades\Route;

Route::get('/', function () {
    return view('welcome');
});

// admin routes
Route::get('/admin', function () {
    return view('admin.index');
})->middleware(['auth', 'verified'])->name('admin');

Route::middleware('auth')->group(function () {
    Route::get('/profile', [ProfileController::class, 'edit'])->name('profile.edit');
    Route::patch('/profile', [ProfileController::class, 'update'])->name('profile.update');
    Route::delete('/profile', [ProfileController::class, 'destroy'])->name('profile.destroy');
});


// school route
Route::get('/admin/schools', [SchoolController::class, 'index'])->name('admin.schools');

Route::post('/admin/schools/upload', [SchoolController::class, 'store'])->name('schools.upload');
Route::put('/admin/schools/update/{school_id}', [SchoolController::class, 'update'])->name('admin.schools.update');
Route::delete('/admin/schools/delete/{school_id}', [SchoolController::class, 'destroy'])->name('admin.schools.delete');

Route::get('/admin/challenges', [ChallengeController::class, 'index'])->name('admin.challenges');
Route::post('/admin/challenges/create', [ChallengeController::class, 'store'])->name('challenges.create');
Route::put('/admin/challenges/update/{challenge_id}', [ChallengeController::class, 'update'])->name('admin.challenges.update');
Route::delete('/admin/challenges/delete/{challenge_id}', [ChallengeController::class, 'destroy'])->name('admin.challenges.delete');


Route::get('/admin/questions', function () {
    return view('admin.questions');
})->name('admin.questions');

Route::get('/admin/answers', function () {
    return view('admin.answers');
})->name('admin.answers');

Route::get('/admin/representatives', [RepresentativeController::class, 'index'])->name('admin.representatives');

Route::get('/admin/participants', function () {
    return view('admin.schools');
})->name('admin.participants');

require __DIR__.'/auth.php';