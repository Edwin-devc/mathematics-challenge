<!-- resources/views/emails/report-email.blade.php -->

<x-mail::message>
# End of Challenge Report

Hello {{ $participant->name }},

Thank you for participating in our challenge. Please find the attached report for the challenge.

Thanks,<br>
{{ config('app.name') }}
</x-mail::message>
