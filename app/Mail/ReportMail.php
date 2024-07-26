<?php

namespace App\Mail;

use Illuminate\Bus\Queueable;
use Illuminate\Contracts\Queue\ShouldQueue;
use Illuminate\Mail\Mailable;
use Illuminate\Mail\Mailables\Content;
use Illuminate\Mail\Mailables\Envelope;
use Illuminate\Queue\SerializesModels;
use App\Models\Participant;
use Barryvdh\DomPDF\Facade\Pdf;
class ReportMail extends Mailable
{
    use Queueable, SerializesModels;

    public $participant;
    public $questions;

    /**
     * Create a new message instance.
     */
    public function __construct(Participant $participant, $questions)
    {
        $this->participant = $participant;
        $this->questions = $questions;
    }

    /**
     * Get the message envelope.
     */
    public function envelope(): Envelope
    {
        return new Envelope(
            subject: 'End of Challenge PDF Report Mail',
        );
    }

    /**
     * Get the message content definition.
     */
    public function content(): Content
    {
        return new Content(
            markdown: 'emails.report-email',
        );
    }

    /**
     * Get the attachments for the message.
     *
     * @return array<\Illuminate\Mail\Attachment>
     */
    public function attachments(): array
    {
        $viewData = [
            'participant' => $this->participant,
            'questions' => $this->questions,
        ];

        $pdfData = Pdf::loadView('emails.report-pdf', $viewData)->output();

        return [
            new \Illuminate\Mail\Attachment(
                function() use ($pdfData) {
                    return $pdfData;
                },
                'report.pdf',
            ),
        ];
    }
}