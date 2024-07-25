<?php

namespace App\Imports;

use App\Models\School;
use App\Models\Representative;
use Maatwebsite\Excel\Concerns\ToModel;
use Maatwebsite\Excel\Concerns\WithHeadingRow;
use Illuminate\Support\Facades\Mail;
use \App\Mail\WelcomeEmail;

class SchoolsImport implements ToModel, WithHeadingRow
{
    /**
     * @param array $row
     *
     * @return \Illuminate\Database\Eloquent\Model|null
     */
    public function model(array $row)
    {
        $random_number = mt_rand(100, 900);
        $school_id = "S" . $random_number;
        $representative_id = "R" . $random_number;

        $school = new School([
            'school_id' => $school_id,
            'name' => $row['name'],
            'district' => $row['district'],
            'registration_number' => $row['registration_number'], 
            'representative_email' => $row['representative_email'],
            'representative_name' => $row['representative_name'] 
        ]);
        $representative = new Representative([
            'representative_id' => $representative_id,
            'name' => $row['representative_name'],
            'email' => $row['representative_email'],
            'password' => $random_number,
            'school_id' => $school_id
        ]);


        // To send emails after registration
        Mail::to($representative->email)->send(new WelcomeEmail($representative, $school->name));

        return [$school, $representative];
    }
}