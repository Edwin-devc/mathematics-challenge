<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use App\Models\School;
use App\Models\SchoolPerformance;

class SchoolPerformancesSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run()
    {
        $schools = School::all();

        if ($schools->isEmpty()) {
            echo "No schools found in the database.\n";
            return;
        }

        $years = range(2015, 2023);

        foreach ($schools as $school) {
            foreach ($years as $year) {
                SchoolPerformance::create([
                    'school_id' => $school->school_id,
                    'year' => $year,
                    'score' => rand(40, 100), // Random scores between 40 and 100
                ]);
            }
        }
    }
}
