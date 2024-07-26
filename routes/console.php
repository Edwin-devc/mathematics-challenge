<?php

use Illuminate\Foundation\Inspiring;
use Illuminate\Support\Facades\Artisan;
use Illuminate\Support\Facades\Schedule;
use Illuminate\Support\Facades\Mail;
use App\Mail\ReportMail;
use App\Models\Challenge;
use App\Models\Participant;
use Carbon\Carbon;

Artisan::command('inspire', function () {
    $this->comment(Inspiring::quote());
})->purpose('Display an inspiring quote')->hourly();

Schedule::call(function () {
    $today = Carbon::now()->toDateString();
    $challenges = Challenge::where('start_date', $today)->get();
    if ($challenges->isEmpty()) {
        return;
    }
    foreach ($challenges as $challenge) {
        $challenge->update(['is_valid' => 'true']);
    }
})->dailyAt('12:00');

Schedule::call(function () {
    $today = Carbon::now()->toDateString();
    $challenges = Challenge::where('end_date', $today)->get();

    if ($challenges->isEmpty()) {
        return;
    }

    foreach ($challenges as $challenge) {
        $challenge->update(['is_valid' => 'false']);
        $questions = $challenge->questions()->with('answers')->get();
        $participants = Participant::where('challenge_id', $challenge->id)->get();

        foreach ($participants as $participant) {
             Mail::to($participant->email)->send(new ReportMail($participant, $questions));
        }
    }
})->dailyAt('12:00');