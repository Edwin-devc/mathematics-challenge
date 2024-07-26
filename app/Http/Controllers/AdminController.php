<?php
namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\SchoolPerformance;
use App\Models\School;
use App\Models\Question;
use App\Models\Challenge;
use App\Models\Participant;
use Illuminate\Support\Facades\DB;

class AdminController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        $years = SchoolPerformance::select('year')->distinct()->orderBy('year')->pluck('year');
        $performances = SchoolPerformance::with('school')->get()->groupBy('school.name')->map(function ($group) {
            return $group->keyBy('year');
        });
        $questions = Question::with('challenge')
            ->orderBy('total_times_answered_correctly', 'desc')
            ->take(5)
            ->get(['question_id', 'text', 'challenge_id', 'total_times_answered_correctly']);
        $total_questions = Question::all();
        $total_schools = School::all();
        $challenges = Challenge::all();
        $participants = Participant::all();

        $results = DB::select("SELECT challenge_title, school_name, average_score, RANK() OVER (PARTITION BY challenge_id ORDER BY average_score ASC) AS rank FROM ( SELECT challenges.title AS challenge_title, schools.name AS school_name, attempts.challenge_id, schools.registration_number, AVG(attempts.total_score) AS average_score FROM schools JOIN participants ON participants.school_registration_number = schools.registration_number JOIN attempts ON attempts.participant_id = participants.participant_id JOIN challenges ON challenges.challenge_id = attempts.challenge_id GROUP BY attempts.challenge_id, schools.registration_number, challenges.title, schools.name ) AS subquery ORDER BY challenge_id, rank");

        $best_schools_results = DB::table('attempts')
            ->join('participants', 'attempts.participant_id', '=', 'participants.participant_id')
            ->join('schools', 'participants.school_registration_number', '=', 'schools.registration_number')
            ->select(
                'schools.name as school_name',
                DB::raw('AVG(attempts.total_score) as average_score')
            )
            ->groupBy('schools.registration_number', 'schools.name')
            ->orderByDesc('average_score')
            ->get();

        // Add rank
        $ranked_results = $best_schools_results->map(function ($item, $key) {
            $item->rank = $key + 1;
            return $item;
        });
        return view('admin.index', compact('years', 'performances', 'questions', 'total_questions', 'total_schools', 'challenges', 'participants', 'results', 'ranked_results'));
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        //
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        //
    }

    /**
     * Display the specified resource.
     */
    public function show(string $id)
    {
        //
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(string $id)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, string $id)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id)
    {
        //
    }
}
