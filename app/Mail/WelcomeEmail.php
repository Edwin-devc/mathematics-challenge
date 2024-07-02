<?php

namespace App\Mail;

// use Faker\Provider\Address;
use App\Models\Representative;
use Illuminate\Bus\Queueable;
use Illuminate\Contracts\Queue\ShouldQueue;
use Illuminate\Mail\Mailable;
use Illuminate\Mail\Mailables\Content;
use Illuminate\Mail\Mailables\Envelope;
use Illuminate\Queue\SerializesModels;
use Illuminate\Mail\Mailables\Address;

class WelcomeEmail extends Mailable
{
    use Queueable, SerializesModels;

    private Representative $representative;
    public $school_name;

    /**
     * Create a new message instance.
     */

    public function __construct(Representative $representative, $school_name)
    {
        $this->representative = $representative;
        $this->school_name = $school_name;
    }

    /**
     * Get the message envelope.
     */
    public function envelope(): Envelope
    {
        return new Envelope(
            from: new Address("erwakasiisi@gmail.com","Rwakasiisi Edwin"),
            subject: 'Welcome to Mathematics Challenge',
        );
    }

    /**
     * Get the message content definition.
     */
    public function content(): Content
    {
        return new Content(
            view: 'emails.welcome-email',
            with: [
                'representative' => $this->representative
            ],
        );
    }

    /**
     * Get the attachments for the message.
     *
     * @return array<int, \Illuminate\Mail\Mailables\Attachment>
     */
    public function attachments(): array
    {
        return [];
    }
}
