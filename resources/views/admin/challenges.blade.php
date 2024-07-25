@extends('admin.layout.layout')
@section('content')
<div class="container-fluid">
    @if (session('success'))
    <div class="alert alert-success text-center alert-dismissible fade show" role="alert">
        {{ session('success') }}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    @endif
    <!--  Row 1 -->
    <div class="card w-100">
        <div class="card-body p-4">
            <h5 class="card-title fw-semibold mb-4">Challenges</h5>
            <div class="btn-group">
                <button type="button" class="btn btn-success text-white" data-bs-toggle="modal" data-bs-target="#exampleModal" data-bs-whatever="@mdo"><span><i class="ti ti-plus"></i></span>
                    Add Challenge</button>
                <button type="button" class="btn btn-primary text-white" data-bs-toggle="modal" data-bs-target="#exampleModalQuestions" data-bs-whatever="@mdo"><span><i class="ti ti-plus"></i></span>
                    Add Questions</button>
                <button type="button" class="btn btn-secondary text-white" data-bs-toggle="modal" data-bs-target="#exampleModalAnswers" data-bs-whatever="@mdo"><span><i class="ti ti-plus"></i></span>
                    Add Answers</button>
            </div>


            {{-- form modal for adding challenge --}}
            <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h1 class="modal-title fs-5" id="exampleModalLabel">New Challenge</h1>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <form id="challenge_form" method="POST" action="{{ route('challenges.create') }}">
                                @csrf
                                <div class="text-center error_msg mb-2"></div>
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <div class="form-floating">
                                            <input type="text" class="form-control" id="title" name="title" placeholder="Title" required />
                                            <label for="title">Title</label>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-floating">
                                            <input type="text" class="form-control" id="datepicker_start" name="start_date" placeholder="Start Date" required />
                                            <label for="start_date">Start Date</label>
                                        </div>
                                    </div>

                                    <div class="col-md-6">
                                        <div class="form-floating">
                                            <input type="text" class="form-control" id="datepicker_end" name="end_date" placeholder="End Date" required />
                                            <label for="end_date">End Date</label>
                                        </div>
                                    </div>

                                    <div class="col-md-6">
                                        <div class="form-floating" id="duration">
                                            <input type="number" min="1" class="form-control" id="duration" placeholder="Duration" name="duration" required />
                                            <label for="duration">Duration (Minutes)</label>
                                        </div>
                                    </div>
                                    <div class="col-12">
                                        <div class="form-floating">
                                            <input type="number" min="1" class="form-control" id="number_of_questions" name="number_of_questions" placeholder="Number of Questions" required />
                                            <label for="number_of_questions">Number of Questions</label>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-danger btn-sm" data-bs-dismiss="modal">Close</button>
                                        <button type="submit" class="btn btn-success btn-sm">Add Challenge</button>
                                    </div>
                                </div>
                            </form>
                        </div>

                    </div>
                </div>
            </div>
            {{-- end form modal --}}

            {{-- form modal for adding questions to the challenge --}}
            <div class="modal fade" id="exampleModalQuestions" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h1 class="modal-title fs-5" id="exampleModalLabel">Add Questions</h1>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <form method="POST" action="{{ route('questions.upload') }}" enctype="multipart/form-data">
                                @csrf
                                <div class="row g-3">
                                    <div class="col-12">
                                        <select name="challenge_id" class="form-select" required>
                                            <option value="">Select challenge</option>
                                            @foreach($challenges as $challenge)
                                            <option value="{{ $challenge->challenge_id }}">{{ $challenge->title }}</option>
                                            @endforeach
                                        </select>
                                    </div>
                                    <div class="col-12">
                                        <div class="form-floating">
                                            <input type="file" class="form-control" id="file" name="file" required />
                                            {{-- <label for="file">Choose Questions File</label> --}}
                                        </div>
                                    </div>

                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-danger btn-sm" data-bs-dismiss="modal">Close</button>
                                        <button type="submit" class="btn mtc-btn-primary btn-sm text-white">Add Questions</button>
                                    </div>
                                </div>
                            </form>
                        </div>

                    </div>
                </div>
            </div>
            {{-- end form modal --}}
            {{-- form modal for adding answers for questions --}}
            <div class="modal fade" id="exampleModalAnswers" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h1 class="modal-title fs-5" id="exampleModalLabel">Add Answers</h1>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <form method="POST" action="{{ route('answers.upload') }}" enctype="multipart/form-data">
                                @csrf
                                <div class="row g-3">

                                    <div class="col-12">
                                        <select name="challenge_id" class="form-select">
                                            <option value="">Select challenge</option>
                                            @foreach($challenges as $challenge)
                                            <option value="{{ $challenge->challenge_id }}">{{ $challenge->title }}</option>
                                            @endforeach
                                        </select>
                                    </div>
                                    <div class="col-12">
                                        <input type="file" class="form-control" id="file" name="file" required />
                                    </div>

                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-danger btn-sm" data-bs-dismiss="modal">Close</button>
                                        <button type="submit" class="btn btn-success btn-sm">Add Answers</button>
                                    </div>
                                </div>
                            </form>
                        </div>

                    </div>
                </div>
            </div>
            {{-- end form modal --}}

            {{-- view model --}}

            <div class="table-responsive">
                <table class="table text-nowrap mb-0 align-middle">
                    <thead class="text-dark fs-4">
                        <tr>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0">ID</h6>
                            </th>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0">Title</h6>
                            </th>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0">Starts On</h6>
                            </th>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0">Ends On</h6>
                            </th>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0">Duration</h6>
                            </th>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0">Questions</h6>
                            </th>
                            <th class="border-bottom-0">
                                <h6 class="fw-semibold mb-0 text-center">Action</h6>
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        @if (count($challenges) === 0)
                        <tr class="text-center">
                            <td class="border-bottom-0" colspan="7">
                                <h6 class="fw-normal mb-0">No Challenges found</h6>
                            </td>
                        </tr>
                        @endif
                        @php
                        $i = 1;
                        @endphp
                        @foreach ($challenges as $challenge)
                        <tr>
                            <td class="border-bottom-0">
                                <h6 class="fw-normal mb-0">{{ $challenge->challenge_id }}</h6>
                            </td>
                            <td class="border-bottom-0">
                                <h6 class="fw-normal mb-0">{{ $challenge->title }}</h6>
                            </td>
                            <td class="border-bottom-0">
                                <h6 class="fw-normal mb-0">{{ $challenge->start_date }}</h6>
                            </td>
                            <td class="border-bottom-0">
                                <p class="mb-0 fw-normal">{{ $challenge->end_date }}</p>
                            </td>
                            <td class="border-bottom-0">
                                <p class="fw-normal mb-0">{{ $challenge->duration }}</p>
                            </td>
                            <td class="border-bottom-0">
                                <p class="fw-normal mb-0">{{ $challenge->number_of_questions }}</p>
                            </td>

                            <td class="border-bottom-0">
                                <p class="fw-bold mb-0 fs-4">
                                    <span class="mx-2 text-success" data-bs-toggle="modal" data-bs-target="#exampleModalview{{ $challenge->challenge_id }}">
                                        <i class="ti ti-eye"></i>
                                    </span>

                                    <span class="me-2 text-primary" data-bs-toggle="modal" data-bs-target="#exampleModaledit{{ $challenge->challenge_id }}" data-bs-whatever="@mdo">
                                        <i class="ti ti-edit"></i>
                                    </span>

                                    <span class="me-2 text-primary" data-bs-toggle="modal" data-bs-target="#exampleModaldelete{{ $challenge->challenge_id }}" data-bs-whatever="@mdo">
                                        <i class="ti ti-trash text-danger"></i>
                                    </span>

                                </p>
                            </td>

                            <!-- view Modal -->
                            <div class="modal fade" id="exampleModalview{{ $challenge->challenge_id }}" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                                <div class="modal-dialog modal-dialog-centered">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h1 class="modal-title fs-5" id="exampleModalLabel{{ $challenge->challenge_id }}">Challenge Details
                                            </h1>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body">
                                            <p><span class="text-primary">Title: </span>{{ $challenge->title }}</p>
                                            <p><span class="text-primary">Starts On: </span>{{ $challenge->start_date }}
                                            </p>
                                            <p><span class="text-primary">Ends On:
                                                </span>{{ $challenge->end_date }}</p>
                                            <p><span class="text-primary">Duration:
                                                </span>{{ $challenge->duration }}</p>
                                            <p><span class="text-primary">Questions:
                                                </span>{{ $challenge->number_of_questions }}</p>
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-primary btn-sm" data-bs-dismiss="modal">Close</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            {{-- end view model --}}


                            {{-- edit form modal --}}
                            <div class="modal fade" id="exampleModaledit{{ $challenge->challenge_id }}" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                                <div class="modal-dialog">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h1 class="modal-title fs-5" id="exampleModalLabel">Update Challenge Details
                                            </h1>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body">
                                            <form method="POST" action="">
                                                @csrf
                                                @method('PUT')
                                                <div class="row g-3">
                                                    <div class="col-md-6">
                                                        <div class="form-floating">
                                                            <input type="text" value="{{ $challenge->title }}" class="form-control" id="title" name="title" placeholder="Title" required />
                                                            <label for="title">Title</label>
                                                        </div>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <div class="form-floating">
                                                            <input type="text" value="{{ $challenge->start_date }}" class="form-control" id="datepicker_start_edit" name="start_date" placeholder="Start Date" required />
                                                            <label for="start_date">Start Date</label>
                                                        </div>
                                                    </div>

                                                    <div class="col-md-6">
                                                        <div class="form-floating">
                                                            <input type="text" value="{{ $challenge->end_date }}" class="form-control" id="datepicker_end_edit" name="end_date" placeholder="End Date" required />
                                                            <label for="end_date">End Date</label>
                                                        </div>
                                                    </div>

                                                    <div class="col-md-6">
                                                        <div class="form-floating" id="duration">
                                                            <input type="number" value="{{ $challenge->duration }}" min="1" class="form-control" id="duration" placeholder="Duration" name="duration" required />
                                                            <label for="duration">Duration (Minutes)</label>
                                                        </div>
                                                    </div>
                                                    <div class="col-12">
                                                        <div class="form-floating">
                                                            <input type="number" value="{{ $challenge->number_of_questions }}" min="1" class="form-control" id="number_of_questions" name="number_of_questions" placeholder="Number of Questions" required />
                                                            <label for="number_of_questions">Number of Questions</label>
                                                        </div>
                                                    </div>
                                                    <div class="modal-footer">
                                                        <button type="button" class="btn btn-danger btn-sm" data-bs-dismiss="modal">Close</button>
                                                        <button type="submit" class="btn btn-success btn-sm">Add Challenge</button>
                                                    </div>
                                                </div>
                                            </form>
                                        </div>

                                    </div>
                                </div>
                            </div>
                            {{-- end edit form modal --}}

                            {{-- delete modal --}}
                            <div class="modal fade" id="exampleModaldelete{{ $challenge->challenge_id }}" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                                <div class="modal-dialog">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h1 class="modal-title fs-5" id="exampleModalLabel">Delete Challenge
                                            </h1>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body">
                                            <p>Are you sure you want to delete {{ $challenge->name }}?</p>
                                            <form action="{{ route('admin.challenges.delete', ['challenge_id' => $challenge->challenge_id]) }}" method="POST" style="display:inline;" class="delete-form">
                                                @csrf
                                                @method('DELETE')
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-sm btn-success" data-bs-dismiss="modal">No</button>
                                                    <button type="submit" class="btn btn-sm btn-danger">Yes</button>
                                                </div>

                                            </form>
                                        </div>

                                    </div>
                                </div>
                            </div>
                            {{-- end delete modal --}}

                            {{-- mark complete modal --}}
                            <div class="modal fade" id="exampleModalcomplete" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                                <div class="modal-dialog">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h1 class="modal-title fs-5" id="exampleModalLabel">Mark Complete
                                            </h1>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body">
                                            <p>Are you sure you want to mark this booking as complete?</p>
                                            <form action="" method="POST" style="display:inline;" class="delete-form">
                                                @csrf
                                                @method('PUT')
                                                <input type="hidden" name="status" value="Closed">
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-sm btn-success" data-bs-dismiss="modal">No</button>
                                                    <button type="submit" class="btn btn-sm btn-danger">Yes</button>
                                                </div>

                                            </form>
                                        </div>

                                    </div>
                                </div>
                            </div>
                            {{-- end mark complete modal --}}
                        </tr>
                        @endforeach

                    </tbody>
                </table>
            </div>
        </div>
    </div>

</div>
@endsection

@section('scripts')
<script>
    $(document).ready(function() {
        $('#datepicker_start').datepicker({
            format: 'dd/mm/yyyy'
            , startDate: new Date()
            , autoclose: true
            , todayHighlight: true
        });
    });
    $(document).ready(function() {
        $('#datepicker_end').datepicker({
            format: 'dd/mm/yyyy'
            , startDate: new Date()
            , autoclose: true
            , todayHighlight: true
        });
    });

    $(document).ready(function() {
        $('#datepicker_start_edit').datepicker({
            format: 'dd/mm/yyyy'
            , startDate: new Date()
            , autoclose: true
            , todayHighlight: true
        });
    });
    $(document).ready(function() {
        $('#datepicker_end_edit').datepicker({
            format: 'dd/mm/yyyy'
            , startDate: new Date()
            , autoclose: true
            , todayHighlight: true
        });
    });

    const form = document.getElementById('challenge_form');
    form.addEventListener('submit', (e) => {
        validate_date(e);
    });

    function convertToDate(dateStr) {
        const [day, month, year] = dateStr.split('/').map(Number);
        return new Date(year, month - 1, day); // Month is zero-indexed
    }

    function validate_date(e) {
        const startDateStr = document.forms['challenge_form'].start_date.value;
        const endDateStr = document.forms['challenge_form'].end_date.value;

        // Convert date strings to Date objects
        const startDate = convertToDate(startDateStr);
        const endDate = convertToDate(endDateStr);

        // Check if end date is before start date
        if (endDate < startDate) {
            e.preventDefault();
            const errormsg = document.querySelector(".error_msg");
            errormsg.innerHTML = "End Date cannot be before Start Date";
            errormsg.style.color = 'red';
        }
    }

</script>
@endsection
