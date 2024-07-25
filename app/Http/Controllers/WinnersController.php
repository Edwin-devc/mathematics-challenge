<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use App\Models\Attempt;

class WinnersController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        $subquery = DB::table('attempts')
            ->select('attempts.*', DB::raw('RANK() OVER(PARTITION BY challenge_id ORDER BY total_score DESC) as rank'))
            ->toSql();

        $results = DB::table(DB::raw("($subquery) as ranked_attempts"))
            ->where('rank', '<=', 2)
            ->join('participants', 'ranked_attempts.participant_id', '=', 'participants.participant_id')
            ->join('challenges', 'ranked_attempts.challenge_id', '=', 'challenges.challenge_id')
            ->select('ranked_attempts.*', 'participants.firstname', 'participants.lastname', 'participants.image_path', 'participants.school_registration_number', 'challenges.title as challenge_name')
            ->get();

        return view('welcome', ['results' => $results]);
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
