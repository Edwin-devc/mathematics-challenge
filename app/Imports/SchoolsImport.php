<?php

namespace App\Imports;

use App\Models\School;
use App\Models\Representative;
use Maatwebsite\Excel\Concerns\ToModel;
use Maatwebsite\Excel\Concerns\WithHeadingRow;

class SchoolsImport implements ToModel, WithHeadingRow
{
    /**
     * @param array $row
     *
     * @return \Illuminate\Database\Eloquent\Model|null
     */
    public function model(array $row)
    {
        $random_number = mt_rand(100,900);
        $school_id = "S" . $random_number;
        $representative_id = "R".$random_number;
        
        $school = new School([
            'school_id' => $school_id,             
            'name' => $row['name'],                       
            'district' => $row['district'],
            'registration_number' => $row['registration_number'], // Adjust to the header names in your file
            'representative_email' => $row['representative_email'], // Adjust to the header names in your file
            'representative_name' => $row['representative_name']  // Adjust to the header names in your file
        ]);
        $representative = new Representative([
            'representative_id' => $representative_id,
            'name' => $row['representative_name'],
            'email'=> $row['representative_email'],
            'password'=> $random_number,
            'school_id'=> $school_id
        ]);
        return array($school, $representative);
    }
}