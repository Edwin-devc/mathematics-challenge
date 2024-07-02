<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\School;
use App\Models\Representative;
use App\Imports\SchoolsImport;
use Maatwebsite\Excel\Facades\Excel;

class SchoolController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        $schools = School::all();
        return view('admin.schools', compact('schools'));
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
        $request->validate([
            'file' => 'required|mimes:xlsx'
        ]);

        Excel::import(new SchoolsImport, $request->file('file'));

        return redirect()->route('admin.schools')->with("success", "Schools added successfully.");
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
    public function update(Request $request, $school_id)
    {
        $request->validate([
            'name' => 'required|string',
            'district' => 'required|string',
            'registration_number' => 'required|string',
            'representative_email' => 'required|email',
            'representative_name' => 'required|string'
        ]);

        // Find the record by 'school_id'
        $record = School::where('school_id', $school_id)->firstOrFail();

        // Update the record with new values
        $record->name = strip_tags($request->input('name'));
        $record->district = strip_tags($request->input('district'));
        $record->registration_number = strip_tags($request->input('registration_number'));
        $record->representative_email = strip_tags($request->input('representative_email'));
        $record->representative_name = strip_tags($request->input('representative_name'));
        $record->save();

        $rep = Representative::where('school_id', $school_id)->firstOrFail ();
        $rep->name = strip_tags($request->input('representative_name'));
        $rep->email = strip_tags($request->input('representative_email'));
        $rep->save();

        return redirect()->route('admin.schools')->with("success", "$record->name updated successfully.");
    }


    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id)
    {
        $record = School::findOrFail($id);
        $deleted_school_name = $record->name;
        $record->delete();
        return redirect()->route('admin.schools')->with("success", "$deleted_school_name deleted successfully.");
    }
}