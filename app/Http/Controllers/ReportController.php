<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Barryvdh\DomPDF\Facade\Pdf;
use Illuminate\Support\Facades\Storage;
use App\Models\Challenge;
use App\Models\Participant;
use App\Mail\ChallengeMail;
use Illuminate\Support\Facades\Mail;
use Carbon\Carbon;

class ReportController extends Controller
{
    public function sendChallengeReport()
    {
        // Check if there's a challenge with ID 10 for today
        // $today = Carbon::today()->toDateString();
        $challenge = Challenge::where('challenge_id', 10)->first();

        if ($challenge) {
            // Retrieve questions and answers
            $questions = $challenge->questions()->with('answer')->get(); // Assuming there's a relationship 'questions' in Challenge model

            $data = [
                'challenge' => $challenge,
                'questions' => $questions
            ];

            // Generate PDF
            $pdf = Pdf::loadView('reports.challenge', $data);
            $filePath = storage_path('app/public/challenge.pdf');
            Storage::put('public/challenge.pdf', $pdf->output());

            // Fetch participants
            $participants = Participant::all();

            // Send email to each participant
            foreach ($participants as $participant) {
                $subject = 'Challenge Report for ' . $challenge->title;
                $body = 'Please find the attached PDF for today\'s challenge report.';
                $attachmentPath = $filePath;

                Mail::to($participant->email)->send(new ChallengeMail($subject, $body, $attachmentPath));
            }

            return 'Emails sent successfully!';
        } else {
            return 'No challenge with ID 10 found for today.';
        }
    }
}
